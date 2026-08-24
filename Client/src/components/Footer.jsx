import { useState, useEffect } from 'react';

export default function Footer({ onOpenBooking }) {
  const [showScrollTop, setShowScrollTop] = useState(false);

  useEffect(() => {
    const onScroll = () => {
      setShowScrollTop(window.scrollY > 400);
    };
    window.addEventListener('scroll', onScroll, { passive: true });
    onScroll();
    return () => window.removeEventListener('scroll', onScroll);
  }, []);

  const scrollToTop = () => {
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  const handleBook = (e) => {
    e.preventDefault();
    if (onOpenBooking) onOpenBooking();
    else window.location.hash = 'booking';
  };

  return (
    <footer className="site-footer">
      <div className="wrap">
        <div className="ft-main">
          {/* Column 1: Brand & Tagline */}
          <div className="ft-col ft-brand-col">
            <div className="ft-logo">SALON</div>
            <p className="ft-tagline">
              Premium men’s grooming studio in London. Dedicated to precision haircuts, custom fades, and tailored styling for your unique look.
            </p>
          </div>

          {/* Column 2: Contact Information */}
          <div className="ft-col ft-contact-col">
            <h4 className="ft-heading">CONTACT</h4>
            <ul className="ft-contact-list">
              <li>
                <span className="ft-icon">
                  <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round" strokeLinejoin="round">
                    <path d="M20 10c0 6-8 12-8 12s-8-6-8-12a8 8 0 0 1 16 0Z"/>
                    <circle cx="12" cy="10" r="3"/>
                  </svg>
                </span>
                <div>
                  18 Redchurch Street<br />
                  London E2 7DP
                </div>
              </li>
              <li>
                <span className="ft-icon">
                  <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round" strokeLinejoin="round">
                    <path d="M22 16.92v3a2 2 0 0 1-2.18 2 19.79 19.79 0 0 1-8.63-3.07 19.5 19.5 0 0 1-6-6 19.79 19.79 0 0 1-3.07-8.67A2 2 0 0 1 4.11 2h3a2 2 0 0 1 2 1.72 12.84 12.84 0 0 0 .7 2.81 2 2 0 0 1-.45 2.11L8.09 9.91a16 16 0 0 0 6 6l1.27-1.27a2 2 0 0 1 2.11-.45 12.84 12.84 0 0 0 2.81.7A2 2 0 0 1 22 16.92z"/>
                  </svg>
                </span>
                <a href="tel:+442071234567">+44 20 7123 4567</a>
              </li>
              <li>
                <span className="ft-icon">
                  <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round" strokeLinejoin="round">
                    <rect width="20" height="16" x="2" y="4" rx="2"/>
                    <path d="m22 7-8.97 5.7a1.94 1.94 0 0 1-2.06 0L2 7"/>
                  </svg>
                </span>
                <a href="mailto:hello@salon.studio">hello@salon.studio</a>
              </li>
            </ul>
          </div>

          {/* Column 3: Follow Us & Connect */}
          <div className="ft-col ft-social-col">
            <h4 className="ft-heading">CONNECT</h4>
            <div className="ft-social-boxes">
              <a href="https://instagram.com" target="_blank" rel="noopener noreferrer" className="ft-social-box" aria-label="Instagram">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round" strokeLinejoin="round">
                  <rect width="20" height="20" x="2" y="2" rx="5" ry="5"/>
                  <path d="M16 11.37A4 4 0 1 1 12.63 8 4 4 0 0 1 16 11.37z"/>
                  <line x1="17.5" x2="17.51" y1="6.5" y2="6.5"/>
                </svg>
              </a>
              <a href="https://facebook.com" target="_blank" rel="noopener noreferrer" className="ft-social-box" aria-label="Facebook">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round" strokeLinejoin="round">
                  <path d="M18 2h-3a5 5 0 0 0-5 5v3H7v4h3v8h4v-8h3l1-4h-4V7a1 1 0 0 1 1-1h3z"/>
                </svg>
              </a>
            </div>
            <button className="btn ft-book-btn" onClick={handleBook}>
              <span>Book Appointment</span>
            </button>
          </div>
        </div>

        {/* Legal & Copyright */}
        <div className="ft-bottom">
          <span>© 2026 SALON MEN'S STUDIO · ALL RIGHTS RESERVED</span>
          <span>MON – SUN · 9AM – 9PM</span>
        </div>
      </div>

      {/* Floating Scroll to Top Button */}
      <button className={`ft-scroll-top${showScrollTop ? ' visible' : ''}`} onClick={scrollToTop} aria-label="Scroll to top">
        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.2" strokeLinecap="round" strokeLinejoin="round">
          <path d="m18 15-6-6-6 6"/>
        </svg>
      </button>
    </footer>
  );
}