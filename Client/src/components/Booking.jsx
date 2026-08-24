import { useState, useRef } from 'react';

export default function Booking() {
  const [submitted, setSubmitted] = useState(false);
  const inputRef = useRef(null);

  // TODO: Integrate with backend API or email service (e.g. Mailchimp, SendGrid).
  // Currently only validates + shows confirmation UI — no data is actually sent.
  const handleSubmit = (e) => {
    e.preventDefault();
    const input = inputRef.current;
    if (!input.value || !input.checkValidity()) {
      input.focus();
      return;
    }
    setSubmitted(true);
    input.value = '';
  };

  return (
    <section id="signup">
      <div className="wrap">
        <h2 className="rv">Your style, your signature.</h2>
        <p className="rv">
          Every appointment is tailored to you. From fresh fades and textured cuts to refined beard grooming, we focus on precision, detail, and a finish that feels uniquely yours.
        </p>
        <form className="rv" onSubmit={handleSubmit} noValidate>
          <input
            ref={inputRef}
            type="email"
            required
            placeholder="you@email.com"
            aria-label="Email address"
          />
          <button className="btn" type="submit"><span>Join Us</span></button>
        </form>
        <div className={`ok${submitted ? ' on' : ''}`} role="status">
          You are on the list. Look out for mail.
        </div>
      </div>
    </section>
  );
}
