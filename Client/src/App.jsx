import { useState, useEffect, useCallback } from 'react';
import Announcement from './components/Announcement';
import Navbar from './components/Navbar';
import Hero from './components/Hero';
import Categories from './components/Categories';
import About from './components/About';
import Services from './components/Services';
import Lookbook from './components/Lookbook';
import Essentials from './components/Essentials';
import Studio from './components/Studio';
import Booking from './components/Booking';
import Footer from './components/Footer';
import BookingModal from './components/BookingModal';
import MyAppointmentsModal from './components/MyAppointmentsModal';
import AdminDashboardModal from './components/AdminDashboardModal';
import useScrollReveal from './hooks/useScrollReveal';
import useParallax from './hooks/useParallax';
import useCountUp from './hooks/useCountUp';
import { api } from './services/api';

export default function App() {
  const [user, setUser] = useState(null);
  const [isBookingOpen, setIsBookingOpen] = useState(false);
  const [isMyAppointmentsOpen, setIsMyAppointmentsOpen] = useState(false);
  const [isAdminDashboardOpen, setIsAdminDashboardOpen] = useState(false);

  /* all scroll-driven effects */
  useScrollReveal();
  useParallax();
  useCountUp();

  // Load current user profile from server on mount
  useEffect(() => {
    const checkAuth = async () => {
      try {
        const userData = await api.getAuthMe();
        setUser(userData);
      } catch (err) {
        // User not logged in yet (normal for guests)
        setUser(null);
      }
    };
    checkAuth();
  }, []);

  const handleLogout = async () => {
    try {
      await api.logout();
    } catch (err) {
      console.error('Logout failed:', err);
    } finally {
      setUser(null);
      window.location.href = '/';
    }
  };

  const openBooking = useCallback(() => {
    setIsBookingOpen(true);
  }, []);

  const closeBooking = useCallback(() => {
    setIsBookingOpen(false);
    if (window.location.hash === '#booking') {
      window.history.replaceState(null, '', window.location.pathname);
    }
  }, []);

  /* Hash routing listener for #booking */
  useEffect(() => {
    const checkHash = () => {
      if (window.location.hash === '#booking') {
        setIsBookingOpen(true);
      }
    };
    checkHash();
    window.addEventListener('hashchange', checkHash);
    return () => window.removeEventListener('hashchange', checkHash);
  }, []);

  return (
    <>
      <Announcement />
      <Navbar
        user={user}
        onOpenBooking={openBooking}
        onOpenMyAppointments={() => setIsMyAppointmentsOpen(true)}
        onOpenAdminDashboard={() => setIsAdminDashboardOpen(true)}
        onLogout={handleLogout}
      />
      <Hero onOpenBooking={openBooking} />
      <Categories />
      <About onOpenBooking={openBooking} />
      <Services />
      <Lookbook />
      <Essentials />
      <Studio />
      <Booking />
      <Footer onOpenBooking={openBooking} />

      {/* Interactive Booking Modal / Page */}
      <BookingModal
        isOpen={isBookingOpen}
        onClose={closeBooking}
        user={user}
      />

      {/* User Appointments History Modal */}
      <MyAppointmentsModal
        isOpen={isMyAppointmentsOpen}
        onClose={() => setIsMyAppointmentsOpen(false)}
        user={user}
      />

      {/* Admin Dashboard Modal */}
      <AdminDashboardModal
        isOpen={isAdminDashboardOpen}
        onClose={() => setIsAdminDashboardOpen(false)}
      />
    </>
  );
}
