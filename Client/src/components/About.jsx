import imgSeason from "../assets/season-about.jpg";

export default function About({ onOpenBooking }) {
  const handleBook = (e) => {
    e.preventDefault();
    if (onOpenBooking) onOpenBooking();
    else window.location.hash = 'booking';
  };

  return (
    <section id="season">
      <div className="grid">
        <div className="copy">
          <div className="kick lbl rv about-kick">About Us</div>
          <h2 className="rv" style={{ marginBottom: 16 }}>The Salon</h2>

          <div className="about-body rv">
            <p className="rv">
              We're a team of passionate stylists dedicated to men's grooming,
              style, and self-expression. From classic cuts to modern trends, we
              bring precision, creativity, and premium care to every service.
            </p>
          </div>

          <a
            href="#booking"
            className="btn rv"
            onClick={handleBook}
          >
            <span>Book Your Appointment</span>
          </a>
        </div>
        <div className="shot">
          <img id="seasonImg" src={imgSeason} alt="New Season" loading="lazy" />
        </div>
      </div>
    </section>
  );
}
