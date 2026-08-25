import { useState, useEffect } from 'react';
import { api } from '../services/api';

const SERVICES = [
  {
    id: 'haircut',
    name: 'Signature Haircut',
    price: 500,
    duration: '30 mins',
    desc: 'Consultation, wash, precision haircut, hot towel finish & styling.',
  },
  {
    id: 'fade',
    name: 'Low Taper / Skin Fade',
    price: 600,
    duration: '30 mins',
    desc: 'Seamless razor fade, line-up, scalp refresh & textured top style.',
  },
  {
    id: 'beard',
    name: 'Beard Sculpting & Trim',
    price: 350,
    duration: '30 mins',
    desc: 'Hot towel prep, straight razor line-up, beard oil & precision shaping.',
  },
  {
    id: 'combo',
    name: 'The Full Experience',
    price: 950,
    duration: '60 mins',
    desc: 'Signature haircut + beard sculpting + facial cleansing & scalp massage.',
  },
  {
    id: 'scalp',
    name: 'Scalp Treatment & Refresh',
    price: 400,
    duration: '30 mins',
    desc: 'Deep cleansing scalp wash, exfoliating treatment & scalp massage.',
  },
];

export default function BookingModal({ isOpen, onClose, user }) {
  const [step, setStep] = useState(1);
  const [selectedService, setSelectedService] = useState(SERVICES[0]);
  const [selectedBarber, setSelectedBarber] = useState(null); // null means any available
  const [availableProviders, setAvailableProviders] = useState([]);
  
  // Date selection
  const dates = Array.from({ length: 7 }, (_, i) => {
    const d = new Date();
    d.setDate(d.getDate() + i);
    return {
      full: d.toISOString().split('T')[0],
      dayName: i === 0 ? 'Today' : i === 1 ? 'Tomorrow' : d.toLocaleDateString('en-US', { weekday: 'short' }),
      dayNum: d.getDate(),
      month: d.toLocaleDateString('en-US', { month: 'short' }),
    };
  });

  const [selectedDate, setSelectedDate] = useState(() => dates[0].full);
  const [availableSlots, setAvailableSlots] = useState([]);
  const [selectedTimeSlot, setSelectedTimeSlot] = useState(null);
  const [loadingAvailability, setLoadingAvailability] = useState(false);
  const [errorMsg, setErrorMsg] = useState('');
  const [processingPayment, setProcessingPayment] = useState(false);

  const [formData, setFormData] = useState({
    name: user ? user.name : '',
    email: user ? user.email : '',
    notes: '',
  });

  const [confirmedAppointment, setConfirmedAppointment] = useState(null);

  // Update form data when user logs in
  useEffect(() => {
    if (user) {
      setFormData((prev) => ({
        ...prev,
        name: user.name || prev.name,
        email: user.email || prev.email,
      }));
    }
  }, [user]);

  // Lock body scroll when modal is open
  useEffect(() => {
    if (isOpen) {
      document.body.style.overflow = 'hidden';
    } else {
      document.body.style.overflow = '';
    }
    return () => { document.body.style.overflow = ''; };
  }, [isOpen]);

  // Fetch real-time availability from backend when date changes
  useEffect(() => {
    if (!isOpen) return;

    const fetchAvailability = async () => {
      setLoadingAvailability(true);
      setErrorMsg('');
      try {
        const response = await api.getAvailability(selectedDate);
        setAvailableSlots(response.slots || []);
        if (response.slots && response.slots.length > 0) {
          const firstAvailable = response.slots.find(s => s.available);
          setSelectedTimeSlot(firstAvailable || null);
          setAvailableProviders(firstAvailable?.availableProviders || []);
        } else {
          setSelectedTimeSlot(null);
          setAvailableProviders([]);
        }
      } catch (err) {
        console.error('Failed to load availability from server:', err);
        setErrorMsg('Unable to fetch live availability from server. Please retry.');
      } finally {
        setLoadingAvailability(false);
      }
    };

    fetchAvailability();
  }, [selectedDate, isOpen]);

  if (!isOpen) return null;

  const handleNext = () => {
    setErrorMsg('');
    if (step === 3 && (!selectedTimeSlot || !selectedTimeSlot.available)) {
      setErrorMsg('Please select an available time slot at least one hour from now.');
      return;
    }
    if (step < 4) setStep(step + 1);
  };

  const handlePrev = () => {
    setErrorMsg('');
    if (step > 1) setStep(step - 1);
  };

  const handleConfirmAndPay = async (e) => {
    e.preventDefault();
    setErrorMsg('');

    if (!user) {
      setErrorMsg('Please login using Google to complete your booking.');
      return;
    }

    if (!selectedTimeSlot || !selectedTimeSlot.available) {
      setErrorMsg('The selected time slot is not available. Please choose another slot.');
      return;
    }

    setProcessingPayment(true);

    try {
      // 1. Create Appointment on backend
      const bookingData = {
        appointmentDate: selectedDate,
        startTime: selectedTimeSlot.startTime,
        amount: selectedService.price,
        providerId: selectedBarber ? selectedBarber.id : null,
      };

      const appointment = await api.bookAppointment(bookingData);

      // 2. Create Razorpay Payment Order on backend
      const order = await api.createPaymentOrder(appointment.id);

      // 3. Launch Razorpay Checkout Modal
      const options = {
        key: order.keyId,
        amount: Math.round(order.amount * 100),
        currency: order.currency,
        name: 'SALON Studio',
        description: `Appointment Ref: ${appointment.appointmentReference}`,
        order_id: order.razorpayOrderId,
        handler: async function (response) {
          try {
            // 4. Verify Payment Signature on backend
            const verificationResult = await api.verifyPayment({
              razorpayOrderId: response.razorpay_order_id,
              razorpayPaymentId: response.razorpay_payment_id,
              razorpaySignature: response.razorpay_signature,
            });

            // Payment & Appointment verified! Show Confirmation step
            setConfirmedAppointment({
              ...appointment,
              razorpayPaymentId: response.razorpay_payment_id,
              status: 'CONFIRMED',
            });
            setStep(5);
          } catch (err) {
            console.error('Payment verification failed:', err);
            setErrorMsg(err.message || 'Payment signature verification failed.');
          } finally {
            setProcessingPayment(false);
          }
        },
        prefill: {
          name: user.name,
          email: user.email,
        },
        theme: {
          color: '#6b21a8',
        },
        modal: {
          ondismiss: function () {
            setProcessingPayment(false);
            setErrorMsg('Payment process was cancelled. Your appointment is pending.');
          },
        },
      };

      if (window.Razorpay) {
        const rzp = new window.Razorpay(options);
        rzp.open();
      } else {
        throw new Error('Razorpay SDK failed to load. Please refresh the page.');
      }
    } catch (err) {
      console.error('Booking error:', err);
      setErrorMsg(err.message || 'Failed to process booking. Please try again.');
      setProcessingPayment(false);
    }
  };

  const resetBooking = () => {
    setStep(1);
    setConfirmedAppointment(null);
    setErrorMsg('');
    onClose();
  };

  return (
    <div className="bk-overlay" role="dialog" aria-modal="true" aria-labelledby="bk-title">
      <div className="bk-modal">
        {/* Header */}
        <div className="bk-header">
          <div>
            <div className="bk-brand">SALON · APPOINTMENT</div>
            <h2 id="bk-title" className="bk-title">
              {step === 5 ? 'Booking Confirmed' : 'Reserve Your Chair'}
            </h2>
          </div>
          <button className="bk-close" onClick={onClose} aria-label="Close booking modal">
            ✕
          </button>
        </div>

        {/* Progress Bar */}
        {step < 5 && (
          <div className="bk-progress">
            <div className={`bk-step${step >= 1 ? ' active' : ''}`}>1. Service</div>
            <div className={`bk-step${step >= 2 ? ' active' : ''}`}>2. Stylist</div>
            <div className={`bk-step${step >= 3 ? ' active' : ''}`}>3. Date & Time</div>
            <div className={`bk-step${step >= 4 ? ' active' : ''}`}>4. Checkout</div>
          </div>
        )}

        {errorMsg && (
          <div style={{ padding: '10px 20px', background: '#fef2f2', color: '#991b1b', borderLeft: '4px solid #ef4444', margin: '10px 24px 0 24px', borderRadius: '4px', fontSize: '14px' }}>
            ⚠️ {errorMsg}
          </div>
        )}

        {/* Content Container */}
        <div className="bk-body">
          {/* STEP 1: Select Service */}
          {step === 1 && (
            <div className="bk-step-content">
              <h3 className="bk-step-title">Select Service</h3>
              <p className="bk-step-desc">Choose from our menu of premium grooming services.</p>
              <div className="bk-services-grid">
                {SERVICES.map((s) => (
                  <div
                    key={s.id}
                    className={`bk-card${selectedService.id === s.id ? ' selected' : ''}`}
                    onClick={() => setSelectedService(s)}
                  >
                    <div className="bk-card-head">
                      <h4>{s.name}</h4>
                      <span className="bk-price">₹{s.price}</span>
                    </div>
                    <p className="bk-card-desc">{s.desc}</p>
                    <div className="bk-card-foot">
                      <span className="bk-duration">⏱ {s.duration}</span>
                      <span className="bk-radio">{selectedService.id === s.id ? '✓ Selected' : 'Select'}</span>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          )}

          {/* STEP 2: Select Barber */}
          {step === 2 && (
            <div className="bk-step-content">
              <h3 className="bk-step-title">Select Stylist / Barber</h3>
              <p className="bk-step-desc">Choose your preferred provider or select any available.</p>
              <div className="bk-barbers-grid">
                <div
                  className={`bk-card bk-barber-card${selectedBarber === null ? ' selected' : ''}`}
                  onClick={() => setSelectedBarber(null)}
                >
                  <div className="bk-avatar">ANY</div>
                  <div className="bk-barber-info">
                    <h4>Any Available Stylist</h4>
                    <span className="bk-role">First available provider slot</span>
                  </div>
                  <span className="bk-radio">{selectedBarber === null ? '✓ Selected' : 'Select'}</span>
                </div>

                {availableProviders.map((b) => (
                  <div
                    key={b.id}
                    className={`bk-card bk-barber-card${selectedBarber?.id === b.id ? ' selected' : ''}`}
                    onClick={() => setSelectedBarber(b)}
                  >
                    <div className="bk-avatar">{b.name.substring(0, 2).toUpperCase()}</div>
                    <div className="bk-barber-info">
                      <h4>{b.name}</h4>
                      <span className="bk-role">{b.specialization || 'Master Barber'}</span>
                    </div>
                    <span className="bk-radio">{selectedBarber?.id === b.id ? '✓ Selected' : 'Select'}</span>
                  </div>
                ))}
              </div>
            </div>
          )}

          {/* STEP 3: Select Date & Time */}
          {step === 3 && (
            <div className="bk-step-content">
              <h3 className="bk-step-title">Select Date & Time</h3>
              <p className="bk-step-desc">Live time slots updated in real-time from our booking server.</p>

              {/* Date Picker */}
              <div className="bk-dates-row">
                {dates.map((d) => (
                  <button
                    key={d.full}
                    className={`bk-date-chip${selectedDate === d.full ? ' selected' : ''}`}
                    onClick={() => setSelectedDate(d.full)}
                  >
                    <span className="bk-date-day">{d.dayName}</span>
                    <span className="bk-date-num">{d.dayNum}</span>
                    <span className="bk-date-month">{d.month}</span>
                  </button>
                ))}
              </div>

              {/* Dynamic Time Slots */}
              <h4 className="bk-subhead">Available Slots ({selectedDate})</h4>
              {loadingAvailability ? (
                <div style={{ padding: '20px', textAlign: 'center', color: '#888' }}>
                  ⌛ Checking real-time server availability...
                </div>
              ) : (
                <div className="bk-times-grid">
                  {availableSlots.map((slot) => {
                    const isSelected = selectedTimeSlot?.startTime === slot.startTime;
                    return (
                      <button
                        key={slot.startTime}
                        disabled={!slot.available}
                        className={`bk-time-chip${isSelected ? ' selected' : ''}${!slot.available ? ' disabled' : ''}`}
                        onClick={() => {
                          setSelectedTimeSlot(slot);
                          if (slot.availableProviders) setAvailableProviders(slot.availableProviders);
                        }}
                        style={!slot.available ? { opacity: 0.4, cursor: 'not-allowed' } : {}}
                      >
                        <div>{slot.startTime}</div>
                        <div style={{ fontSize: '11px', marginTop: '2px' }}>
                          {slot.available ? `${slot.availableProvidersCount} seats` : 'Unavailable'}
                        </div>
                      </button>
                    );
                  })}
                </div>
              )}
            </div>
          )}

          {/* STEP 4: Review & Checkout */}
          {step === 4 && (
            <div className="bk-step-content bk-two-col">
              <div className="bk-form">
                <h3 className="bk-step-title">Checkout & Payment</h3>
                <p className="bk-step-desc">Secure checkout powered by Razorpay Gateway.</p>

                {!user ? (
                  <div style={{ padding: '16px 20px', background: '#fffbe6', border: '1px solid #ffe58f', borderLeft: '5px solid #faad14', borderRadius: '0px', marginBottom: '22px' }}>
                    <p style={{ margin: '0 0 12px 0', fontSize: '13.5px', color: '#101010', fontWeight: '500' }}>🔒 You must sign in with Google to confirm your reservation and receive email updates.</p>
                    <button type="button" className="btn" onClick={api.loginWithGoogle}>
                      <span>Sign in with Google</span>
                    </button>
                  </div>
                ) : (
                  <div
                    style={{
                      padding: '14px 20px',
                      background: '#f4fbf7',
                      border: '1px solid #5bbd86',
                      borderLeft: '5px solid #2ecc71',
                      borderRadius: '0px',
                      marginBottom: '22px',
                    }}
                  >
                    <div
                      style={{
                        font: '800 10.5px/1 Archivo, sans-serif',
                        letterSpacing: '0.16em',
                        textTransform: 'uppercase',
                        color: '#1e7e4c',
                        marginBottom: '8px',
                      }}
                    >
                      AUTHENTICATED USER:
                    </div>
                    <div
                      style={{
                        display: 'flex',
                        alignItems: 'center',
                        gap: '10px',
                        fontSize: '14px',
                        fontWeight: '600',
                        color: '#101010',
                        fontFamily: 'Archivo, sans-serif',
                      }}
                    >
                      <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="#101010" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                        <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
                        <circle cx="12" cy="7" r="4"></circle>
                      </svg>
                      <span>
                        {user.name} ({user.email})
                      </span>
                    </div>
                  </div>
                )}

                <div className="bk-field">
                  <label htmlFor="bk-notes">Special Requests / Preferences (Optional)</label>
                  <textarea
                    id="bk-notes"
                    rows="3"
                    placeholder="Any specific requests for your cut or beard styling..."
                    value={formData.notes}
                    onChange={(e) => setFormData({ ...formData, notes: e.target.value })}
                  />
                </div>
              </div>

              {/* Summary Card */}
              <div className="bk-summary-card">
                <h4>Booking Summary</h4>
                <div className="bk-summary-line">
                  <span>Service:</span>
                  <strong>{selectedService.name}</strong>
                </div>
                <div className="bk-summary-line">
                  <span>Stylist:</span>
                  <strong>{selectedBarber ? selectedBarber.name : 'Any Available'}</strong>
                </div>
                <div className="bk-summary-line">
                  <span>Date:</span>
                  <strong>{selectedDate}</strong>
                </div>
                <div className="bk-summary-line">
                  <span>Time Slot:</span>
                  <strong>{selectedTimeSlot?.startTime} - {selectedTimeSlot?.endTime}</strong>
                </div>
                <div className="bk-summary-total">
                  <span>Total Amount:</span>
                  <strong>₹{selectedService.price}</strong>
                </div>
                <div className="bk-location-note">
                  💳 <strong>Razorpay Secured</strong> · Instant Email Confirmation
                </div>
              </div>
            </div>
          )}

          {/* STEP 5: Confirmation Screen */}
          {step === 5 && (
            <div className="bk-success-screen">
              <div className="bk-success-icon">✓</div>
              <h3>Appointment Confirmed!</h3>
              <p className="bk-success-subtitle">Your appointment has been registered and paid successfully.</p>

              <div className="bk-ref-badge">
                <span>BOOKING REFERENCE</span>
                <strong>{confirmedAppointment?.appointmentReference || 'SALON-CONFIRMED'}</strong>
              </div>

              <div className="bk-success-details">
                <div className="bk-detail-item">
                  <span>Customer Name</span>
                  <strong>{user?.name}</strong>
                </div>
                <div className="bk-detail-item">
                  <span>Service</span>
                  <strong>{selectedService.name} (₹{selectedService.price})</strong>
                </div>
                <div className="bk-detail-item">
                  <span>Stylist Assigned</span>
                  <strong>{confirmedAppointment?.provider?.name || (selectedBarber ? selectedBarber.name : 'Salon Stylist')}</strong>
                </div>
                <div className="bk-detail-item">
                  <span>Date & Time</span>
                  <strong>{selectedDate} at {selectedTimeSlot?.startTime}</strong>
                </div>
                <div className="bk-detail-item">
                  <span>Razorpay Payment ID</span>
                  <strong style={{ fontSize: '13px', wordBreak: 'break-all' }}>{confirmedAppointment?.razorpayPaymentId || 'PAID'}</strong>
                </div>
              </div>

              <p className="bk-email-notice">
                An email confirmation has been sent to <strong>{user?.email}</strong>.
              </p>

              <div className="bk-success-actions">
                <button className="btn" onClick={resetBooking}>
                  <span>Done</span>
                </button>
              </div>
            </div>
          )}
        </div>

        {/* Footer Actions */}
        {step < 5 && (
          <div className="bk-footer">
            {step > 1 ? (
              <button className="btn-line" onClick={handlePrev} disabled={processingPayment}>
                ← Back
              </button>
            ) : <div />}

            {step < 4 ? (
              <button className="btn" onClick={handleNext}>
                <span>Next Step →</span>
              </button>
            ) : (
              <button
                className="btn"
                type="button"
                onClick={handleConfirmAndPay}
                disabled={processingPayment || !user || !selectedTimeSlot?.available}
              >
                <span>{processingPayment ? 'Processing Payment...' : `Pay ₹${selectedService.price} via Razorpay`}</span>
              </button>
            )}
          </div>
        )}
      </div>
    </div>
  );
}
