import { useState, useEffect } from 'react';
import { api } from '../services/api';

export default function AdminDashboardModal({ isOpen, onClose }) {
  const [activeTab, setActiveTab] = useState('overview');
  const [dashboard, setDashboard] = useState(null);
  const [appointments, setAppointments] = useState([]);
  const [providers, setProviders] = useState([]);
  const [salonConfig, setSalonConfig] = useState(null);
  const [notificationLogs, setNotificationLogs] = useState([]);
  const [loading, setLoading] = useState(false);
  const [errorMsg, setErrorMsg] = useState('');

  // Provider Form State
  const [newProvider, setNewProvider] = useState({ name: '', phone: '', email: '', specialization: 'Master Barber' });

  // Salon Config Form State
  const [configForm, setConfigForm] = useState({ openingTime: '10:00:00', closingTime: '20:00:00', slotDurationMinutes: 30, maxConcurrentAppointmentsPerSlot: 5 });

  useEffect(() => {
    if (!isOpen) return;

    loadAdminData();
  }, [isOpen, activeTab]);

  const loadAdminData = async () => {
    setLoading(true);
    setErrorMsg('');
    try {
      if (activeTab === 'overview') {
        const data = await api.getAdminDashboard();
        setDashboard(data);
      } else if (activeTab === 'appointments') {
        const res = await api.getAdminAppointments();
        setAppointments(res.content || []);
      } else if (activeTab === 'providers') {
        try {
          const res = await api.getAdminProviders();
          setProviders(res || []);
        } catch (e) {
          console.warn('Falling back to getSalonConfig for providers:', e);
          const cfg = await api.getSalonConfig();
          setProviders(cfg.providers || []);
        }
      } else if (activeTab === 'config') {
        const res = await api.getSalonConfig();
        setSalonConfig(res);
        setConfigForm({
          openingTime: res.openingTime || '10:00:00',
          closingTime: res.closingTime || '20:00:00',
          slotDurationMinutes: res.slotDurationMinutes || 30,
          maxConcurrentAppointmentsPerSlot: res.maxConcurrentAppointmentsPerSlot || 5,
        });
      } else if (activeTab === 'notifications') {
        const res = await api.getNotificationLogs();
        setNotificationLogs(res || []);
      }
    } catch (err) {
      console.error('Failed to load admin data:', err);
      setErrorMsg(err.message || 'Access denied or server error loading admin panel.');
    } finally {
      setLoading(false);
    }
  };

  if (!isOpen) return null;

  const handleAddProvider = async (e) => {
    e.preventDefault();
    if (!newProvider.name) return;
    try {
      await api.addProvider(newProvider);
      setNewProvider({ name: '', phone: '', email: '', specialization: 'Master Barber' });
      loadAdminData();
    } catch (err) {
      alert(err.message || 'Failed to add provider');
    }
  };

  const handleToggleProviderStatus = async (providerId, currentStatus) => {
    const nextStatus = currentStatus === 'AVAILABLE' ? 'UNAVAILABLE' : 'AVAILABLE';
    try {
      await api.updateProviderStatus(providerId, nextStatus);
      loadAdminData();
    } catch (err) {
      alert(err.message || 'Failed to update provider status');
    }
  };

  const handleDeleteProvider = async (providerId) => {
    if (!window.confirm('Are you sure you want to delete this barber?')) return;
    try {
      await api.deleteProvider(providerId);
      loadAdminData();
    } catch (err) {
      alert(err.message || 'Failed to delete provider');
    }
  };

  const handleUpdateConfig = async (e) => {
    e.preventDefault();
    try {
      await api.updateSalonConfig(configForm);
      alert('Salon operating parameters updated successfully!');
      loadAdminData();
    } catch (err) {
      alert(err.message || 'Failed to update configuration');
    }
  };

  const formatNotificationType = (type) => {
    switch (type) {
      case 'CONFIRMATION_CUSTOMER':
        return 'Customer Confirmation Email';
      case 'CONFIRMATION_ADMIN':
        return 'Admin Alert Email';
      case 'CANCELLATION_CUSTOMER':
        return 'Customer Cancellation Email';
      default:
        return type || 'System Notification';
    }
  };

  return (
    <div
      className="bk-overlay"
      role="dialog"
      aria-modal="true"
      style={{
        background: 'rgba(16, 16, 16, 0.85)',
        backdropFilter: 'blur(4px)',
        display: 'grid',
        placeItems: 'center',
        padding: '16px',
      }}
    >
      <div
        className="bk-modal"
        style={{
          maxWidth: '1100px',
          width: '100%',
          height: '88vh',
          background: '#efede8',
          color: '#101010',
          borderRadius: '0px',
          border: '1px solid #101010',
          boxShadow: '0 30px 70px rgba(0, 0, 0, 0.5)',
          overflow: 'hidden',
          display: 'flex',
          flexDirection: 'column',
          fontFamily: 'Archivo, sans-serif',
        }}
      >
        {/* Sharp Ink Header */}
        <div
          className="bk-header"
          style={{
            background: '#101010',
            color: '#efede8',
            padding: '24px 30px',
            display: 'flex',
            justifyContent: 'space-between',
            alignItems: 'center',
            borderRadius: '0px',
            borderBottom: '1px solid #101010',
          }}
        >
          <div>
            <div
              style={{
                font: '500 10.5px/1 Archivo, sans-serif',
                letterSpacing: '0.22em',
                textTransform: 'uppercase',
                color: '#8a8781',
                marginBottom: '6px',
              }}
            >
              ADMINISTRATION PANEL
            </div>
            <h2
              className="bk-title"
              style={{
                color: '#efede8',
                fontFamily: 'Archivo, sans-serif',
                fontSize: '22px',
                fontWeight: '800',
                letterSpacing: '-0.02em',
                textTransform: 'uppercase',
                margin: 0,
              }}
            >
              SALON MANAGEMENT DASHBOARD
            </h2>
          </div>
          <button
            className="bk-close"
            onClick={onClose}
            style={{
              color: '#efede8',
              fontSize: '18px',
              background: 'none',
              border: '1px solid rgba(239, 237, 232, 0.3)',
              borderRadius: '0px',
              width: '38px',
              height: '38px',
              cursor: 'pointer',
              display: 'grid',
              placeItems: 'center',
            }}
          >
            ✕
          </button>
        </div>

        {/* Sharp Tab Bar */}
        <div
          style={{
            display: 'flex',
            borderBottom: '1px solid rgba(16, 16, 16, 0.14)',
            background: '#e4e1d7',
            padding: '0 30px',
            overflowX: 'auto',
          }}
        >
          {[
            { id: 'overview', label: 'Overview' },
            { id: 'appointments', label: 'Appointments' },
            { id: 'providers', label: 'Barbers / Stylists' },
            { id: 'config', label: 'Operating Hours' },
            { id: 'notifications', label: 'Notification Logs' },
          ].map((t) => (
            <button
              key={t.id}
              onClick={() => setActiveTab(t.id)}
              style={{
                padding: '16px 24px',
                border: 'none',
                background: 'none',
                borderBottom: activeTab === t.id ? '3px solid #101010' : '3px solid transparent',
                fontWeight: activeTab === t.id ? '800' : '600',
                color: activeTab === t.id ? '#101010' : '#8a8781',
                cursor: 'pointer',
                fontSize: '11px',
                fontFamily: 'Archivo, sans-serif',
                letterSpacing: '0.16em',
                textTransform: 'uppercase',
                transition: 'all 0.2s ease',
                borderRadius: '0px',
              }}
            >
              {t.label}
            </button>
          ))}
        </div>

        {/* Scrollable Body */}
        <div className="bk-body" style={{ padding: '30px', overflowY: 'auto', flex: 1, color: '#101010' }}>
          {errorMsg && (
            <div
              style={{
                padding: '14px 18px',
                background: '#fce8e6',
                color: '#c5221f',
                borderRadius: '0px',
                border: '1px solid #c5221f',
                marginBottom: '24px',
                fontWeight: '600',
                fontSize: '13px',
              }}
            >
              ⚠️ {errorMsg}
            </div>
          )}

          {loading ? (
            <div
              style={{
                textAlign: 'center',
                padding: '60px 0',
                color: '#8a8781',
                fontSize: '12px',
                letterSpacing: '0.15em',
                textTransform: 'uppercase',
                fontWeight: '600',
              }}
            >
              ⌛ FETCHING RECORDS FROM SERVER...
            </div>
          ) : (
            <>
              {/* TAB 1: OVERVIEW */}
              {activeTab === 'overview' && dashboard && (
                <div>
                  <div
                    style={{
                      display: 'grid',
                      gridTemplateColumns: 'repeat(4, 1fr)',
                      gap: '14px',
                      marginBottom: '36px',
                    }}
                  >
                    <div
                      style={{
                        background: '#ffffff',
                        border: '1px solid #101010',
                        borderRadius: '0px',
                        padding: '22px',
                        boxShadow: '4px 4px 0px rgba(16,16,16,0.05)',
                      }}
                    >
                      <div
                        style={{
                          font: '700 10px/1 Archivo, sans-serif',
                          letterSpacing: '0.2em',
                          textTransform: 'uppercase',
                          color: '#8a8781',
                        }}
                      >
                        TODAY'S APPOINTMENTS
                      </div>
                      <div style={{ fontSize: '36px', fontWeight: '800', color: '#101010', marginTop: '10px' }}>
                        {dashboard.todaysAppointments ?? 0}
                      </div>
                    </div>
                    <div
                      style={{
                        background: '#ffffff',
                        border: '1px solid #101010',
                        borderRadius: '0px',
                        padding: '22px',
                        boxShadow: '4px 4px 0px rgba(16,16,16,0.05)',
                      }}
                    >
                      <div
                        style={{
                          font: '700 10px/1 Archivo, sans-serif',
                          letterSpacing: '0.2em',
                          textTransform: 'uppercase',
                          color: '#8a8781',
                        }}
                      >
                        TOTAL REVENUE
                      </div>
                      <div style={{ fontSize: '36px', fontWeight: '800', color: '#101010', marginTop: '10px' }}>
                        ₹{dashboard.totalRevenue ?? 0}
                      </div>
                    </div>
                    <div
                      style={{
                        background: '#ffffff',
                        border: '1px solid #101010',
                        borderRadius: '0px',
                        padding: '22px',
                        boxShadow: '4px 4px 0px rgba(16,16,16,0.05)',
                      }}
                    >
                      <div
                        style={{
                          font: '700 10px/1 Archivo, sans-serif',
                          letterSpacing: '0.2em',
                          textTransform: 'uppercase',
                          color: '#8a8781',
                        }}
                      >
                        REGISTERED USERS
                      </div>
                      <div style={{ fontSize: '36px', fontWeight: '800', color: '#101010', marginTop: '10px' }}>
                        {dashboard.totalUsers ?? 0}
                      </div>
                    </div>
                    <div
                      style={{
                        background: '#ffffff',
                        border: '1px solid #101010',
                        borderRadius: '0px',
                        padding: '22px',
                        boxShadow: '4px 4px 0px rgba(16,16,16,0.05)',
                      }}
                    >
                      <div
                        style={{
                          font: '700 10px/1 Archivo, sans-serif',
                          letterSpacing: '0.2em',
                          textTransform: 'uppercase',
                          color: '#8a8781',
                        }}
                      >
                        ACTIVE BARBERS
                      </div>
                      <div style={{ fontSize: '36px', fontWeight: '800', color: '#101010', marginTop: '10px' }}>
                        {dashboard.availableProvidersCount ?? dashboard.totalProvidersCount ?? 0}
                      </div>
                    </div>
                  </div>

                  <h4
                    style={{
                      font: '700 14px/1 Archivo, sans-serif',
                      letterSpacing: '0.14em',
                      textTransform: 'uppercase',
                      color: '#101010',
                      margin: '0 0 16px 0',
                    }}
                  >
                    RECENT BOOKING ACTIVITY
                  </h4>
                  <div style={{ background: '#ffffff', borderRadius: '0px', border: '1px solid #101010', overflow: 'hidden' }}>
                    <table style={{ width: '100%', borderCollapse: 'collapse', fontSize: '13.5px', color: '#101010' }}>
                      <thead>
                        <tr style={{ background: '#101010', color: '#efede8', textAlign: 'left' }}>
                          <th style={{ padding: '14px 18px', font: '700 10.5px/1 Archivo', letterSpacing: '0.16em', textTransform: 'uppercase' }}>REF #</th>
                          <th style={{ padding: '14px 18px', font: '700 10.5px/1 Archivo', letterSpacing: '0.16em', textTransform: 'uppercase' }}>CLIENT</th>
                          <th style={{ padding: '14px 18px', font: '700 10.5px/1 Archivo', letterSpacing: '0.16em', textTransform: 'uppercase' }}>DATE & TIME</th>
                          <th style={{ padding: '14px 18px', font: '700 10.5px/1 Archivo', letterSpacing: '0.16em', textTransform: 'uppercase' }}>AMOUNT</th>
                          <th style={{ padding: '14px 18px', font: '700 10.5px/1 Archivo', letterSpacing: '0.16em', textTransform: 'uppercase' }}>STATUS</th>
                        </tr>
                      </thead>
                      <tbody>
                        {dashboard.recentAppointments?.map((app) => (
                          <tr key={app.id} style={{ borderBottom: '1px solid rgba(16, 16, 16, 0.12)' }}>
                            <td style={{ padding: '14px 18px', fontWeight: '800', color: '#101010' }}>#{app.appointmentReference}</td>
                            <td style={{ padding: '14px 18px', fontWeight: '600' }}>{app.userName || 'Customer'}</td>
                            <td style={{ padding: '14px 18px', color: '#555' }}>{app.appointmentDate} at {app.startTime}</td>
                            <td style={{ padding: '14px 18px', fontWeight: '800' }}>₹{app.amount}</td>
                            <td style={{ padding: '14px 18px' }}>
                              <span
                                style={{
                                  padding: '4px 10px',
                                  borderRadius: '0px',
                                  fontSize: '10px',
                                  fontWeight: '800',
                                  letterSpacing: '0.1em',
                                  textTransform: 'uppercase',
                                  background: app.status === 'CONFIRMED' ? '#e6f4ea' : '#fce8e6',
                                  color: app.status === 'CONFIRMED' ? '#137333' : '#c5221f',
                                  border: `1px solid ${app.status === 'CONFIRMED' ? '#137333' : '#c5221f'}`,
                                }}
                              >
                                {app.status}
                              </span>
                            </td>
                          </tr>
                        ))}
                      </tbody>
                    </table>
                  </div>
                </div>
              )}

              {/* TAB 2: APPOINTMENTS */}
              {activeTab === 'appointments' && (
                <div>
                  <h4
                    style={{
                      font: '700 14px/1 Archivo, sans-serif',
                      letterSpacing: '0.14em',
                      textTransform: 'uppercase',
                      color: '#101010',
                      margin: '0 0 16px 0',
                    }}
                  >
                    MASTER APPOINTMENTS REGISTER
                  </h4>
                  <div style={{ background: '#ffffff', borderRadius: '0px', border: '1px solid #101010', overflow: 'hidden' }}>
                    <table style={{ width: '100%', borderCollapse: 'collapse', fontSize: '13.5px', color: '#101010' }}>
                      <thead>
                        <tr style={{ background: '#101010', color: '#efede8', textAlign: 'left' }}>
                          <th style={{ padding: '14px 18px', font: '700 10.5px/1 Archivo', letterSpacing: '0.16em', textTransform: 'uppercase' }}>REF #</th>
                          <th style={{ padding: '14px 18px', font: '700 10.5px/1 Archivo', letterSpacing: '0.16em', textTransform: 'uppercase' }}>CLIENT INFO</th>
                          <th style={{ padding: '14px 18px', font: '700 10.5px/1 Archivo', letterSpacing: '0.16em', textTransform: 'uppercase' }}>STYLIST</th>
                          <th style={{ padding: '14px 18px', font: '700 10.5px/1 Archivo', letterSpacing: '0.16em', textTransform: 'uppercase' }}>DATE & TIME</th>
                          <th style={{ padding: '14px 18px', font: '700 10.5px/1 Archivo', letterSpacing: '0.16em', textTransform: 'uppercase' }}>AMOUNT</th>
                          <th style={{ padding: '14px 18px', font: '700 10.5px/1 Archivo', letterSpacing: '0.16em', textTransform: 'uppercase' }}>STATUS</th>
                        </tr>
                      </thead>
                      <tbody>
                        {appointments.map((app) => (
                          <tr key={app.id} style={{ borderBottom: '1px solid rgba(16, 16, 16, 0.12)' }}>
                            <td style={{ padding: '14px 18px', fontWeight: '800', color: '#101010' }}>#{app.appointmentReference}</td>
                            <td style={{ padding: '14px 18px' }}>
                              <div style={{ fontWeight: '700' }}>{app.user?.name || 'Customer'}</div>
                              <div style={{ fontSize: '12px', color: '#777' }}>{app.user?.email}</div>
                            </td>
                            <td style={{ padding: '14px 18px', color: '#333' }}>{app.provider?.name || 'Unassigned'}</td>
                            <td style={{ padding: '14px 18px', color: '#555' }}>{app.appointmentDate} @ {app.startTime}</td>
                            <td style={{ padding: '14px 18px', fontWeight: '800' }}>₹{app.amount}</td>
                            <td style={{ padding: '14px 18px' }}>
                              <span
                                style={{
                                  padding: '4px 10px',
                                  borderRadius: '0px',
                                  fontSize: '10px',
                                  fontWeight: '800',
                                  letterSpacing: '0.1em',
                                  textTransform: 'uppercase',
                                  background: app.status === 'CONFIRMED' ? '#e6f4ea' : '#fce8e6',
                                  color: app.status === 'CONFIRMED' ? '#137333' : '#c5221f',
                                  border: `1px solid ${app.status === 'CONFIRMED' ? '#137333' : '#c5221f'}`,
                                }}
                              >
                                {app.status}
                              </span>
                            </td>
                          </tr>
                        ))}
                      </tbody>
                    </table>
                  </div>
                </div>
              )}

              {/* TAB 3: BARBERS / STYLISTS */}
              {activeTab === 'providers' && (
                <div>
                  {/* Add Provider Card */}
                  <div
                    style={{
                      marginBottom: '32px',
                      background: '#ffffff',
                      padding: '26px',
                      borderRadius: '0px',
                      border: '1px solid #101010',
                      boxShadow: '4px 4px 0px rgba(16,16,16,0.05)',
                    }}
                  >
                    <h4
                      style={{
                        margin: '0 0 18px 0',
                        font: '700 13px/1 Archivo, sans-serif',
                        letterSpacing: '0.16em',
                        textTransform: 'uppercase',
                        color: '#101010',
                      }}
                    >
                      ➕ ADD NEW STYLIST / BARBER
                    </h4>
                    <form onSubmit={handleAddProvider} style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))', gap: '14px' }}>
                      <input
                        type="text"
                        placeholder="FULL NAME *"
                        required
                        value={newProvider.name}
                        onChange={(e) => setNewProvider({ ...newProvider, name: e.target.value })}
                        style={{
                          padding: '12px 14px',
                          background: '#faf8f5',
                          color: '#101010',
                          border: '1px solid #101010',
                          borderRadius: '0px',
                          fontSize: '13px',
                          fontFamily: 'Archivo, sans-serif',
                        }}
                      />
                      <input
                        type="text"
                        placeholder="SPECIALIZATION (E.G. FADE MASTER)"
                        value={newProvider.specialization}
                        onChange={(e) => setNewProvider({ ...newProvider, specialization: e.target.value })}
                        style={{
                          padding: '12px 14px',
                          background: '#faf8f5',
                          color: '#101010',
                          border: '1px solid #101010',
                          borderRadius: '0px',
                          fontSize: '13px',
                          fontFamily: 'Archivo, sans-serif',
                        }}
                      />
                      <input
                        type="email"
                        placeholder="EMAIL (OPTIONAL)"
                        value={newProvider.email}
                        onChange={(e) => setNewProvider({ ...newProvider, email: e.target.value })}
                        style={{
                          padding: '12px 14px',
                          background: '#faf8f5',
                          color: '#101010',
                          border: '1px solid #101010',
                          borderRadius: '0px',
                          fontSize: '13px',
                          fontFamily: 'Archivo, sans-serif',
                        }}
                      />
                      <button
                        type="submit"
                        style={{
                          background: '#101010',
                          color: '#efede8',
                          border: '1px solid #101010',
                          borderRadius: '0px',
                          font: '700 11px/1 Archivo, sans-serif',
                          letterSpacing: '0.18em',
                          textTransform: 'uppercase',
                          padding: '14px 20px',
                          cursor: 'pointer',
                        }}
                      >
                        + ADD BARBER
                      </button>
                    </form>
                  </div>

                  {/* Providers Roster */}
                  <h4
                    style={{
                      font: '700 14px/1 Archivo, sans-serif',
                      letterSpacing: '0.14em',
                      textTransform: 'uppercase',
                      color: '#101010',
                      margin: '0 0 16px 0',
                    }}
                  >
                    BARBERS & STYLISTS ROSTER ({providers.length})
                  </h4>
                  <div style={{ background: '#ffffff', borderRadius: '0px', border: '1px solid #101010', overflow: 'hidden' }}>
                    <table style={{ width: '100%', borderCollapse: 'collapse', fontSize: '13.5px', color: '#101010' }}>
                      <thead>
                        <tr style={{ background: '#101010', color: '#efede8', textAlign: 'left' }}>
                          <th style={{ padding: '14px 18px', font: '700 10.5px/1 Archivo', letterSpacing: '0.16em', textTransform: 'uppercase' }}>NAME</th>
                          <th style={{ padding: '14px 18px', font: '700 10.5px/1 Archivo', letterSpacing: '0.16em', textTransform: 'uppercase' }}>SPECIALIZATION</th>
                          <th style={{ padding: '14px 18px', font: '700 10.5px/1 Archivo', letterSpacing: '0.16em', textTransform: 'uppercase' }}>EMAIL</th>
                          <th style={{ padding: '14px 18px', font: '700 10.5px/1 Archivo', letterSpacing: '0.16em', textTransform: 'uppercase' }}>STATUS</th>
                          <th style={{ padding: '14px 18px', font: '700 10.5px/1 Archivo', letterSpacing: '0.16em', textTransform: 'uppercase' }}>ACTIONS</th>
                        </tr>
                      </thead>
                      <tbody>
                        {providers.map((p) => (
                          <tr key={p.id} style={{ borderBottom: '1px solid rgba(16, 16, 16, 0.12)' }}>
                            <td style={{ padding: '14px 18px', fontWeight: '800', color: '#101010' }}>{p.name}</td>
                            <td style={{ padding: '14px 18px', color: '#555' }}>{p.specialization || 'Master Barber'}</td>
                            <td style={{ padding: '14px 18px', color: '#666' }}>{p.email || 'N/A'}</td>
                            <td style={{ padding: '14px 18px' }}>
                              <span
                                style={{
                                  padding: '4px 10px',
                                  borderRadius: '0px',
                                  fontSize: '10px',
                                  fontWeight: '800',
                                  letterSpacing: '0.1em',
                                  textTransform: 'uppercase',
                                  background: p.status === 'AVAILABLE' ? '#e6f4ea' : '#fce8e6',
                                  color: p.status === 'AVAILABLE' ? '#137333' : '#c5221f',
                                  border: `1px solid ${p.status === 'AVAILABLE' ? '#137333' : '#c5221f'}`,
                                }}
                              >
                                {p.status}
                              </span>
                            </td>
                            <td style={{ padding: '14px 18px' }}>
                              <div style={{ display: 'flex', gap: '8px' }}>
                                <button
                                  type="button"
                                  className="btn-action-outline"
                                  style={{ padding: '8px 12px', fontSize: '10px' }}
                                  onClick={() => handleToggleProviderStatus(p.id, p.status)}
                                >
                                  TOGGLE STATUS
                                </button>
                                <button
                                  type="button"
                                  className="btn-danger-outline"
                                  style={{ padding: '8px 12px', fontSize: '10px' }}
                                  onClick={() => handleDeleteProvider(p.id)}
                                >
                                  DELETE
                                </button>
                              </div>
                            </td>
                          </tr>
                        ))}
                      </tbody>
                    </table>
                  </div>
                </div>
              )}

              {/* TAB 4: OPERATING HOURS & CONFIG */}
              {activeTab === 'config' && (
                <div
                  style={{
                    maxWidth: '540px',
                    background: '#ffffff',
                    padding: '30px',
                    borderRadius: '0px',
                    border: '1px solid #101010',
                    boxShadow: '4px 4px 0px rgba(16,16,16,0.05)',
                  }}
                >
                  <h4
                    style={{
                      font: '700 14px/1 Archivo, sans-serif',
                      letterSpacing: '0.14em',
                      textTransform: 'uppercase',
                      color: '#101010',
                      margin: '0 0 20px 0',
                    }}
                  >
                    SALON OPERATING PARAMETERS
                  </h4>
                  <form onSubmit={handleUpdateConfig} style={{ display: 'flex', flexDirection: 'column', gap: '18px' }}>
                    <div>
                      <label
                        style={{
                          display: 'block',
                          font: '700 10.5px/1 Archivo, sans-serif',
                          letterSpacing: '0.16em',
                          textTransform: 'uppercase',
                          marginBottom: '8px',
                          color: '#101010',
                        }}
                      >
                        OPENING TIME (HH:mm:ss):
                      </label>
                      <input
                        type="text"
                        value={configForm.openingTime}
                        onChange={(e) => setConfigForm({ ...configForm, openingTime: e.target.value })}
                        style={{
                          width: '100%',
                          padding: '12px 14px',
                          background: '#faf8f5',
                          color: '#101010',
                          border: '1px solid #101010',
                          borderRadius: '0px',
                          fontSize: '13.5px',
                        }}
                      />
                    </div>
                    <div>
                      <label
                        style={{
                          display: 'block',
                          font: '700 10.5px/1 Archivo, sans-serif',
                          letterSpacing: '0.16em',
                          textTransform: 'uppercase',
                          marginBottom: '8px',
                          color: '#101010',
                        }}
                      >
                        CLOSING TIME (HH:mm:ss):
                      </label>
                      <input
                        type="text"
                        value={configForm.closingTime}
                        onChange={(e) => setConfigForm({ ...configForm, closingTime: e.target.value })}
                        style={{
                          width: '100%',
                          padding: '12px 14px',
                          background: '#faf8f5',
                          color: '#101010',
                          border: '1px solid #101010',
                          borderRadius: '0px',
                          fontSize: '13.5px',
                        }}
                      />
                    </div>
                    <div>
                      <label
                        style={{
                          display: 'block',
                          font: '700 10.5px/1 Archivo, sans-serif',
                          letterSpacing: '0.16em',
                          textTransform: 'uppercase',
                          marginBottom: '8px',
                          color: '#101010',
                        }}
                      >
                        SLOT DURATION (MINUTES):
                      </label>
                      <input
                        type="number"
                        value={configForm.slotDurationMinutes}
                        onChange={(e) => setConfigForm({ ...configForm, slotDurationMinutes: parseInt(e.target.value) })}
                        style={{
                          width: '100%',
                          padding: '12px 14px',
                          background: '#faf8f5',
                          color: '#101010',
                          border: '1px solid #101010',
                          borderRadius: '0px',
                          fontSize: '13.5px',
                        }}
                      />
                    </div>
                    <div>
                      <label
                        style={{
                          display: 'block',
                          font: '700 10.5px/1 Archivo, sans-serif',
                          letterSpacing: '0.16em',
                          textTransform: 'uppercase',
                          marginBottom: '8px',
                          color: '#101010',
                        }}
                      >
                        MAX CONCURRENT SEATS PER SLOT:
                      </label>
                      <input
                        type="number"
                        value={configForm.maxConcurrentAppointmentsPerSlot}
                        onChange={(e) => setConfigForm({ ...configForm, maxConcurrentAppointmentsPerSlot: parseInt(e.target.value) })}
                        style={{
                          width: '100%',
                          padding: '12px 14px',
                          background: '#faf8f5',
                          color: '#101010',
                          border: '1px solid #101010',
                          borderRadius: '0px',
                          fontSize: '13.5px',
                        }}
                      />
                    </div>
                    <button
                      type="submit"
                      style={{
                        background: '#101010',
                        color: '#efede8',
                        border: '1px solid #101010',
                        borderRadius: '0px',
                        font: '700 11px/1 Archivo, sans-serif',
                        letterSpacing: '0.18em',
                        textTransform: 'uppercase',
                        padding: '14px 20px',
                        cursor: 'pointer',
                        marginTop: '10px',
                      }}
                    >
                      SAVE PARAMETERS
                    </button>
                  </form>
                </div>
              )}

              {/* TAB 5: NOTIFICATION LOGS */}
              {activeTab === 'notifications' && (
                <div>
                  <h4
                    style={{
                      font: '700 14px/1 Archivo, sans-serif',
                      letterSpacing: '0.14em',
                      textTransform: 'uppercase',
                      color: '#101010',
                      margin: '0 0 16px 0',
                    }}
                  >
                    SYSTEM EMAIL NOTIFICATIONS LOG
                  </h4>
                  <div style={{ background: '#ffffff', borderRadius: '0px', border: '1px solid #101010', overflow: 'hidden' }}>
                    <table style={{ width: '100%', borderCollapse: 'collapse', fontSize: '13.5px', color: '#101010' }}>
                      <thead>
                        <tr style={{ background: '#101010', color: '#efede8', textAlign: 'left' }}>
                          <th style={{ padding: '14px 18px', font: '700 10.5px/1 Archivo', letterSpacing: '0.16em', textTransform: 'uppercase' }}>RECIPIENT EMAIL</th>
                          <th style={{ padding: '14px 18px', font: '700 10.5px/1 Archivo', letterSpacing: '0.16em', textTransform: 'uppercase' }}>NOTIFICATION EVENT</th>
                          <th style={{ padding: '14px 18px', font: '700 10.5px/1 Archivo', letterSpacing: '0.16em', textTransform: 'uppercase' }}>SENT TIMESTAMP</th>
                          <th style={{ padding: '14px 18px', font: '700 10.5px/1 Archivo', letterSpacing: '0.16em', textTransform: 'uppercase' }}>STATUS</th>
                        </tr>
                      </thead>
                      <tbody>
                        {notificationLogs.map((log) => (
                          <tr key={log.id} style={{ borderBottom: '1px solid rgba(16, 16, 16, 0.12)' }}>
                            <td style={{ padding: '14px 18px', fontWeight: '700', color: '#101010' }}>{log.recipient || 'N/A'}</td>
                            <td style={{ padding: '14px 18px', fontWeight: '500' }}>{formatNotificationType(log.type)}</td>
                            <td style={{ padding: '14px 18px', color: '#555' }}>
                              {log.sentAt ? log.sentAt.replace('T', ' ') : (log.createdAt ? log.createdAt.replace('T', ' ') : 'Pending')}
                            </td>
                            <td style={{ padding: '14px 18px' }}>
                              <span
                                style={{
                                  padding: '4px 10px',
                                  borderRadius: '0px',
                                  fontSize: '10px',
                                  fontWeight: '800',
                                  letterSpacing: '0.1em',
                                  textTransform: 'uppercase',
                                  background: log.status === 'SENT' ? '#e6f4ea' : (log.status === 'FAILED' ? '#fce8e6' : '#fef08a'),
                                  color: log.status === 'SENT' ? '#137333' : (log.status === 'FAILED' ? '#c5221f' : '#854d0e'),
                                  border: `1px solid ${log.status === 'SENT' ? '#137333' : (log.status === 'FAILED' ? '#c5221f' : '#854d0e')}`,
                                }}
                              >
                                {log.status}
                              </span>
                            </td>
                          </tr>
                        ))}
                      </tbody>
                    </table>
                  </div>
                </div>
              )}
            </>
          )}
        </div>
      </div>
    </div>
  );
}
