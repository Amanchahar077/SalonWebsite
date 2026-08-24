import { useState, useCallback, useEffect } from 'react';
import { api } from '../services/api';

function UserAvatar({ user }) {
  const [imgError, setImgError] = useState(false);

  const initial = user?.name ? user.name.charAt(0).toUpperCase() : 'U';

  if (user?.profileImage && !imgError) {
    return (
      <div
        title={user.name}
        style={{
          width: '36px',
          height: '36px',
          borderRadius: '50%',
          overflow: 'hidden',
          border: '1.5px solid #111',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          flexShrink: 0,
          background: '#f3f4f6',
        }}
      >
        <img
          src={user.profileImage}
          alt=""
          referrerPolicy="no-referrer"
          onError={() => setImgError(true)}
          style={{ width: '100%', height: '100%', objectFit: 'cover' }}
        />
      </div>
    );
  }

  return (
    <div
      title={user?.name || 'User'}
      style={{
        width: '36px',
        height: '36px',
        borderRadius: '50%',
        background: 'linear-gradient(135deg, #111827 0%, #374151 100%)',
        color: '#f9fafb',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        fontSize: '14px',
        fontWeight: 'bold',
        letterSpacing: '0.5px',
        border: '1.5px solid #111',
        flexShrink: 0,
        boxShadow: '0 2px 4px rgba(0,0,0,0.1)',
      }}
    >
      {initial}
    </div>
  );
}

export default function Navbar({ user, onOpenBooking, onOpenMyAppointments, onOpenAdminDashboard, onLogout }) {
  const [open, setOpen] = useState(false);

  const toggle = useCallback(() => setOpen((o) => !o), []);
  const close = useCallback(() => setOpen(false), []);

  /* Close menu on ESC key */
  useEffect(() => {
    const onKey = (e) => { if (e.key === 'Escape') close(); };
    window.addEventListener('keydown', onKey);
    return () => window.removeEventListener('keydown', onKey);
  }, [close]);

  /* Prevent body scroll when menu is open */
  useEffect(() => {
    document.body.style.overflow = open ? 'hidden' : '';
    return () => { document.body.style.overflow = ''; };
  }, [open]);

  const scrollTo = (id) => (e) => {
    e.preventDefault();
    close();
    document.getElementById(id)?.scrollIntoView({ behavior: 'smooth' });
  };

  const handleBook = (e) => {
    e.preventDefault();
    close();
    if (onOpenBooking) onOpenBooking();
    else window.location.hash = 'booking';
  };

  return (
    <nav>
      <div className="wrap bar">
        <ul className="nav-links">
          <li><a href="#season" onClick={scrollTo('season')}>About</a></li>
          <li><a href="#look" onClick={scrollTo('look')}>LookBook</a></li>
          <li><a href="#signup" onClick={scrollTo('signup')}>Contact</a></li>
        </ul>

        <div className="mk">SALON</div>

        <div className="util" style={{ display: 'flex', gap: '10px', alignItems: 'center' }}>
          <button className="nav-btn" onClick={handleBook}>
            Book Appointment
          </button>

          {user ? (
            <>
              <button className="nav-btn" onClick={onOpenMyAppointments}>
                My Bookings
              </button>

              {user.role === 'ADMIN' && (
                <button
                  className="nav-btn"
                  onClick={onOpenAdminDashboard}
                >
                  Admin Panel
                </button>
              )}

              <button className="nav-btn" onClick={onLogout} title="Logout of account">
                Logout
              </button>

              <UserAvatar user={user} />
            </>
          ) : (
            <button
              className="nav-btn"
              onClick={api.loginWithGoogle}
            >
              Sign in with Google
            </button>
          )}
        </div>

        {/* Hamburger toggle — visible only on mobile */}
        <button
          className={`hamburger${open ? ' active' : ''}`}
          onClick={toggle}
          aria-expanded={open}
          aria-label="Toggle navigation menu"
        >
          <span className="hb-line" />
          <span className="hb-line" />
          <span className="hb-line" />
        </button>
      </div>

      {/* Mobile overlay menu */}
      <div className={`mob-menu${open ? ' open' : ''}`}>
        <a href="#season" onClick={scrollTo('season')}>About</a>
        <a href="#look" onClick={scrollTo('look')}>LookBook</a>
        <a href="#signup" onClick={scrollTo('signup')}>Contact</a>

        {user ? (
          <>
            <a href="#my-appointments" onClick={() => { close(); onOpenMyAppointments(); }}>
              My Bookings ({user.name})
            </a>
            {user.role === 'ADMIN' && (
              <a href="#admin" onClick={() => { close(); onOpenAdminDashboard(); }}>
                Admin Panel
              </a>
            )}
            <a href="#logout" onClick={() => { close(); onLogout(); }}>
              Logout ({user.email})
            </a>
          </>
        ) : (
          <a href="#login" onClick={() => { close(); api.loginWithGoogle(); }}>
            Sign in with Google
          </a>
        )}

        <a href="#booking" className="btn mob-cta" onClick={handleBook}>
          <span>Book Appointment</span>
        </a>
      </div>

      {/* Backdrop */}
      {open && <div className="mob-backdrop" onClick={close} />}
    </nav>
  );
}
