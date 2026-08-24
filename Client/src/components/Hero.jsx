import { useEffect } from "react";
import heroScissors from "../assets/hero-scissors.png";

export default function Hero({ onOpenBooking }) {
  useEffect(() => {
    const RM = window.matchMedia("(prefers-reduced-motion: reduce)").matches;
    if (RM) {
      document.querySelectorAll(".hv").forEach((e) => e.classList.add("on"));
      return;
    }
    const order = ["h1", "hm", "h2", "h3"];
    order.forEach((id, i) => {
      const el = document.getElementById(id);
      if (el) setTimeout(() => el.classList.add("on"), 120 + i * 140);
    });
  }, []);

  const handleBook = (e) => {
    e.preventDefault();
    if (onOpenBooking) onOpenBooking();
    else window.location.hash = "booking";
  };

  return (
    <header id="hero">
      <div className="wrap" style={{ width: "100%" }}>
        <div className="tl hv" id="h1">
          <p>
            Beauty.
            <br />
            Confidence.
            <br />
            You.
          </p>
          <div className="rule"></div>
        </div>

        <div className="stage">
          <div className="word back" aria-hidden="true">
            <b id="wb">SALON</b>
          </div>
          <img
            className="model hv"
            id="hm"
            src={heroScissors}
            alt="Black barber scissors"
          />
        </div>

        <div className="br hv" id="h2">
          <p>
            Salon
            <br />&<br />
            Studio
          </p>
        </div>

        <div className="acts hv" id="h3">
          <a href="#booking" className="btn" onClick={handleBook}>
            <span>Book now &rarr;</span>
          </a>
          <a href="#season" className="btn-line">
            Explore vibes
          </a>
        </div>
      </div>
    </header>
  );
}
