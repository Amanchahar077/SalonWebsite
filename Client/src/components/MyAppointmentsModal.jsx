import { useState, useEffect } from 'react';
import { api } from '../services/api';

export default function MyAppointmentsModal({ isOpen, onClose, user }) {
  const [appointments, setAppointments] = useState([]);
  const [loading, setLoading] = useState(false);
  const [errorMsg, setErrorMsg] = useState('');
  const [cancellingId, setCancellingId] = useState(null);
  const [cancelReason, setCancelReason] = useState('');
  const [cancelModalId, setCancelModalId] = useState(null);

  // Status Filter State
  const [filters, setFilters] = useState({
    upcoming: true,
    completed: true,
    cancelled: true,
  });

  useEffect(() => {
    if (!isOpen || !user) return;

    const fetchAppointments = async () => {
      setLoading(true);
      setErrorMsg('');
      try {
        const data = await api.getMyAppointments();
        setAppointments(data || []);
      } catch (err) {
        console.error('Failed to fetch user appointments:', err);
        setErrorMsg('Unable to load your appointments.');
      } finally {
        setLoading(false);
      }
    };

    fetchAppointments();
  }, [isOpen, user]);

  if (!isOpen) return null;

  const handleCancelSubmit = async (appointmentId) => {
    setCancellingId(appointmentId);
    try {
      await api.cancelAppointment(appointmentId, cancelReason || 'User requested cancellation');
      const updated = await api.getMyAppointments();
      setAppointments(updated || []);
      setCancelModalId(null);
      setCancelReason('');
    } catch (err) {
      alert(err.message || 'Failed to cancel appointment');
    } finally {
      setCancellingId(null);
    }
  };

  const toggleFilter = (key) => {
    setFilters((prev) => ({ ...prev, [key]: !prev[key] }));
  };

  const filteredAppointments = appointments.filter((app) => {
    if (app.status === 'CONFIRMED' || app.status === 'PENDING_PAYMENT') return filters.upcoming;
    if (app.status === 'COMPLETED') return filters.completed;
    if (app.status === 'CANCELLED') return filters.cancelled;
    return true;
  });

  const getStatusBadge = (status) => {
    switch (status) {
      case 'CONFIRMED':
        return (
          <span
            style={{
              padding: '4px 10px',
              borderRadius: '0px',
              fontSize: '10px',
              fontWeight: '800',
              letterSpacing: '0.12em',
              textTransform: 'uppercase',
              background: '#e6f4ea',
              color: '#137333',
              border: '1px solid #137333',
            }}
          >
            CONFIRMED
          </span>
        );
      case 'COMPLETED':
        return (
          <span
            style={{
              padding: '4px 10px',
              borderRadius: '0px',
              fontSize: '10px',
              fontWeight: '800',
              letterSpacing: '0.12em',
              textTransform: 'uppercase',
              background: '#f3f4f6',
              color: '#555555',
              border: '1px solid #888888',
            }}
          >
            COMPLETED
          </span>
        );
      case 'CANCELLED':
        return (
          <span
            style={{
              padding: '4px 10px',
              borderRadius: '0px',
              fontSize: '10px',
              fontWeight: '800',
              letterSpacing: '0.12em',
              textTransform: 'uppercase',
              background: '#fce8e6',
              color: '#c5221f',
              border: '1px solid #c5221f',
            }}
          >
            CANCELLED
          </span>
        );
      default:
        return (
          <span
            style={{
              padding: '4px 10px',
              borderRadius: '0px',
              fontSize: '10px',
              fontWeight: '800',
              letterSpacing: '0.12em',
              textTransform: 'uppercase',
              background: '#fef08a',
              color: '#854d0e',
              border: '1px solid #854d0e',
            }}
          >
            {status}
          </span>
        );
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
          maxWidth: '1040px',
          width: '100%',
          maxHeight: '90vh',
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
        {/* Top Header Row */}
        <div
          style={{
            background: '#efede8',
            padding: '30px 36px 20px 36px',
            display: 'flex',
            justifyContent: 'space-between',
            alignItems: 'flex-start',
            borderBottom: '1px solid rgba(16, 16, 16, 0.12)',
          }}
        >
          <div>
            <h2
              style={{
                fontFamily: 'Archivo, sans-serif',
                fontSize: '28px',
                fontWeight: '900',
                letterSpacing: '-0.02em',
                textTransform: 'uppercase',
                margin: '0 0 6px 0',
                color: '#101010',
              }}
            >
              YOUR RESERVATION HISTORY
            </h2>
            <p
              style={{
                fontSize: '13.5px',
                color: '#666666',
                margin: 0,
                maxWidth: '560px',
              }}
            >
              Review your upcoming appointments and past services. Manage your time with precision.
            </p>
          </div>
          <button
            className="bk-close"
            onClick={onClose}
            style={{
              color: '#101010',
              fontSize: '18px',
              background: 'none',
              border: '1px solid #101010',
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

        {/* Scrollable Body - 2 Columns (Sidebar + Cards) */}
        <div
          className="bk-body"
          style={{
            padding: '30px 36px',
            overflowY: 'auto',
            flex: 1,
            display: 'grid',
            gridTemplateColumns: '220px 1fr',
            gap: '28px',
            alignItems: 'start',
          }}
        >
          {/* Left Column: Filter Sidebar */}
          <div
            style={{
              background: '#ffffff',
              border: '1px solid #101010',
              borderRadius: '0px',
              padding: '22px',
              boxShadow: '4px 4px 0px rgba(16,16,16,0.04)',
            }}
          >
            <div
              style={{
                font: '700 10.5px/1 Archivo, sans-serif',
                letterSpacing: '0.18em',
                textTransform: 'uppercase',
                color: '#101010',
                paddingBottom: '12px',
                marginBottom: '16px',
                borderBottom: '1px solid rgba(16, 16, 16, 0.15)',
              }}
            >
              FILTER BY STATUS
            </div>
            <div style={{ display: 'flex', flexDirection: 'column', gap: '14px' }}>
              <label
                style={{
                  display: 'flex',
                  alignItems: 'center',
                  gap: '10px',
                  fontSize: '13.5px',
                  fontWeight: '600',
                  color: '#101010',
                  cursor: 'pointer',
                  userSelect: 'none',
                }}
              >
                <input
                  type="checkbox"
                  checked={filters.upcoming}
                  onChange={() => toggleFilter('upcoming')}
                  style={{
                    width: '16px',
                    height: '16px',
                    accentColor: '#101010',
                    cursor: 'pointer',
                  }}
                />
                Upcoming
              </label>
              <label
                style={{
                  display: 'flex',
                  alignItems: 'center',
                  gap: '10px',
                  fontSize: '13.5px',
                  fontWeight: '600',
                  color: '#101010',
                  cursor: 'pointer',
                  userSelect: 'none',
                }}
              >
                <input
                  type="checkbox"
                  checked={filters.completed}
                  onChange={() => toggleFilter('completed')}
                  style={{
                    width: '16px',
                    height: '16px',
                    accentColor: '#101010',
                    cursor: 'pointer',
                  }}
                />
                Completed
              </label>
              <label
                style={{
                  display: 'flex',
                  alignItems: 'center',
                  gap: '10px',
                  fontSize: '13.5px',
                  fontWeight: '600',
                  color: '#101010',
                  cursor: 'pointer',
                  userSelect: 'none',
                }}
              >
                <input
                  type="checkbox"
                  checked={filters.cancelled}
                  onChange={() => toggleFilter('cancelled')}
                  style={{
                    width: '16px',
                    height: '16px',
                    accentColor: '#101010',
                    cursor: 'pointer',
                  }}
                />
                Cancelled
              </label>
            </div>
          </div>

          {/* Right Column: Reservation Cards List */}
          <div>
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
                ⌛ FETCHING RESERVATION HISTORY...
              </div>
            ) : filteredAppointments.length === 0 ? (
              <div
                style={{
                  textAlign: 'center',
                  padding: '60px 20px',
                  background: '#ffffff',
                  border: '1px solid #101010',
                  borderRadius: '0px',
                }}
              >
                <p
                  style={{
                    font: '700 15px/1 Archivo, sans-serif',
                    letterSpacing: '0.12em',
                    textTransform: 'uppercase',
                    margin: '0 0 8px 0',
                    color: '#101010',
                  }}
                >
                  NO MATCHING RESERVATIONS
                </p>
                <p style={{ fontSize: '13px', color: '#666', margin: 0 }}>
                  Adjust your filter settings or book a new appointment.
                </p>
              </div>
            ) : (
              <div style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>
                {filteredAppointments.map((app) => {
                  const isCancelled = app.status === 'CANCELLED';
                  const isCompleted = app.status === 'COMPLETED';

                  return (
                    <div
                      key={app.id}
                      style={{
                        background: '#ffffff',
                        border: '1px solid #101010',
                        borderRadius: '0px',
                        padding: '24px 28px',
                        boxShadow: '4px 4px 0px rgba(16, 16, 16, 0.04)',
                        opacity: isCancelled ? 0.65 : 1,
                        transition: 'all 0.2s ease',
                      }}
                    >
                      {/* Top Row: Ref & Badge on Left, Price on Right */}
                      <div
                        style={{
                          display: 'flex',
                          justifyContent: 'space-between',
                          alignItems: 'flex-start',
                          marginBottom: '10px',
                        }}
                      >
                        <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
                          <span
                            style={{
                              font: '800 13px/1 Archivo, sans-serif',
                              letterSpacing: '0.06em',
                              color: isCancelled ? '#888888' : '#101010',
                            }}
                          >
                            #{app.appointmentReference}
                          </span>
                          {getStatusBadge(app.status)}
                        </div>
                        <div
                          style={{
                            fontSize: '26px',
                            fontWeight: '900',
                            color: isCancelled ? '#a0a0a0' : '#101010',
                            letterSpacing: '-0.02em',
                          }}
                        >
                          ₹{app.amount || 500}
                        </div>
                      </div>

                      {/* Main Title: Service Name */}
                      <h3
                        style={{
                          fontFamily: 'Archivo, sans-serif',
                          fontSize: '21px',
                          fontWeight: '800',
                          letterSpacing: '-0.01em',
                          color: isCancelled ? '#777777' : '#101010',
                          margin: '0 0 12px 0',
                          textDecoration: isCancelled ? 'line-through' : 'none',
                        }}
                      >
                        {app.serviceName || 'Signature Cut & Grooming'}
                      </h3>

                      {/* Sub-details & Action Buttons */}
                      <div
                        style={{
                          display: 'flex',
                          justifyContent: 'space-between',
                          alignItems: 'center',
                          flexWrap: 'wrap',
                          gap: '16px',
                        }}
                      >
                        <div
                          style={{
                            display: 'flex',
                            alignItems: 'center',
                            gap: '16px',
                            fontSize: '13px',
                            color: isCancelled ? '#888888' : '#555555',
                          }}
                        >
                          <span>
                            📅 <strong>{app.appointmentDate}</strong> · {app.startTime}
                          </span>
                          <span>
                            ✂ Stylist: <strong>{app.provider ? app.provider.name : 'Salon Stylist'}</strong>
                          </span>
                        </div>

                        {/* Action Buttons */}
                        <div style={{ display: 'flex', gap: '10px' }}>
                          {app.status === 'CONFIRMED' && (
                            <>
                              <button
                                type="button"
                                className="btn-action-outline"
                                style={{ padding: '8px 14px', fontSize: '10px' }}
                                onClick={() => alert('Reschedule requested! Please contact salon desk or select a new date.')}
                              >
                                RESCHEDULE
                              </button>
                              <button
                                type="button"
                                className="btn-danger-outline"
                                style={{ padding: '8px 14px', fontSize: '10px' }}
                                onClick={() => setCancelModalId(app.id)}
                              >
                                CANCEL
                              </button>
                            </>
                          )}

                          {isCompleted && (
                            <button
                              type="button"
                              style={{
                                background: '#101010',
                                color: '#efede8',
                                border: '1px solid #101010',
                                borderRadius: '0px',
                                padding: '8px 18px',
                                font: '800 10px/1 Archivo, sans-serif',
                                letterSpacing: '0.14em',
                                textTransform: 'uppercase',
                                cursor: 'pointer',
                              }}
                              onClick={() => alert('Rebooking service... redirecting to appointment calendar.')}
                            >
                              REBOOK
                            </button>
                          )}
                        </div>
                      </div>
                    </div>
                  );
                })}
              </div>
            )}
          </div>

          {/* Sharp Cancellation Modal */}
          {cancelModalId && (
            <div
              style={{
                position: 'fixed',
                inset: 0,
                backgroundColor: 'rgba(16, 16, 16, 0.75)',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                zIndex: 10000,
                padding: '16px',
              }}
            >
              <div
                style={{
                  background: '#efede8',
                  padding: '28px',
                  borderRadius: '0px',
                  border: '1px solid #101010',
                  width: '100%',
                  maxWidth: '440px',
                  boxShadow: '8px 8px 0px rgba(16,16,16,0.2)',
                }}
              >
                <h4
                  style={{
                    margin: '0 0 10px 0',
                    font: '800 16px/1 Archivo, sans-serif',
                    letterSpacing: '0.1em',
                    textTransform: 'uppercase',
                    color: '#101010',
                  }}
                >
                  CANCEL APPOINTMENT
                </h4>
                <p style={{ fontSize: '13.5px', color: '#555', margin: '0 0 18px 0' }}>
                  Are you sure you want to cancel this booking? This slot will be released back into system availability.
                </p>
                <div style={{ marginBottom: '20px' }}>
                  <label
                    style={{
                      display: 'block',
                      font: '700 10.5px/1 Archivo, sans-serif',
                      letterSpacing: '0.14em',
                      textTransform: 'uppercase',
                      marginBottom: '8px',
                      color: '#101010',
                    }}
                  >
                    REASON (OPTIONAL):
                  </label>
                  <input
                    type="text"
                    style={{
                      width: '100%',
                      padding: '12px',
                      background: '#ffffff',
                      color: '#101010',
                      border: '1px solid #101010',
                      borderRadius: '0px',
                      fontSize: '13.5px',
                      fontFamily: 'Archivo, sans-serif',
                    }}
                    placeholder="e.g. Schedule clash"
                    value={cancelReason}
                    onChange={(e) => setCancelReason(e.target.value)}
                  />
                </div>
                <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '12px' }}>
                  <button
                    className="btn-line"
                    style={{ padding: '12px 18px', fontSize: '10.5px' }}
                    onClick={() => setCancelModalId(null)}
                    disabled={cancellingId !== null}
                  >
                    KEEP APPOINTMENT
                  </button>
                  <button
                    type="button"
                    style={{
                      background: '#101010',
                      color: '#efede8',
                      border: '1px solid #101010',
                      borderRadius: '0px',
                      font: '700 10.5px/1 Archivo, sans-serif',
                      letterSpacing: '0.14em',
                      textTransform: 'uppercase',
                      padding: '12px 18px',
                      cursor: 'pointer',
                    }}
                    onClick={() => handleCancelSubmit(cancelModalId)}
                    disabled={cancellingId !== null}
                  >
                    {cancellingId !== null ? 'CANCELLING...' : 'CONFIRM CANCEL'}
                  </button>
                </div>
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
