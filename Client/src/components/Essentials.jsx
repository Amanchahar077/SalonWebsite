import imgVisionBoard from "../assets/cloth-barber.jpg";

export default function Essentials() {
  return (
    <section id="cloth">
      <div className="cgrid">
        <div className="ccopy">
          <div
            className="lbl rv"
            style={{ marginBottom: 16, color: "var(--mid)" }}
          >
            The Basics
          </div>
          <h2 className="rv">
            The Mens
            <br />
            Essentials
          </h2>
          <p className="rv">
            We believe great grooming is about more than just a haircut. Every
            style is carefully crafted with precision, quality products, and
            attention to detail, creating a look that stays sharp long after you
            leave the chair.
          </p>
          <dl className="rv">
            <div>
              <dt>Hair Wash</dt>
              <dd>Refresh · Cleanse · Condition</dd>
            </div>

            <div>
              <dt>Haircut</dt>
              <dd>Fade · Taper · Styling</dd>
            </div>

            <div>
              <dt>Beard Trim</dt>
              <dd>Shape · Detail · Finish</dd>
            </div>
          </dl>
        </div>
        <figure className="cshot rv" data-speed="0.05">
          <img
            src={imgVisionBoard}
            alt="Close up of hair clipper trimming head"
            loading="lazy"
          />
        </figure>
      </div>
    </section>
  );
}
