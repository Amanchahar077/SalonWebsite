import imgTenHairCare from "../assets/atelier-barber.jpg";

export default function Studio() {
  return (
    <section id="atelier">
      <div className="agrid">
        <figure className="ashot rv" data-speed="0.05">
          <img
            src={imgTenHairCare}
            alt="Barber trimming fade with clippers and comb"
            loading="lazy"
          />
        </figure>

        <div className="acopy">
          <div
            className="lbl rv"
            style={{
              marginBottom: 16,
              color: 'rgba(239, 237, 232, 0.5)',
            }}
          >
            The Studio
          </div>

          <h2 className="rv">
            Four barbers,
            <br />
            one standard.
          </h2>

          <p className="rv">
            Every cut is crafted with precision, from modern fades and
            textured styles to classic cuts and detailed beard work. We take
            the time to understand your style and create a look made for you.
          </p>

          <div className="acount rv">
            <div>
              <b data-to="4">0</b>
              <span>Barbers</span>
            </div>

            <div>
              <b data-to="25">0</b>
              <span>Styles mastered</span>
            </div>

            <div>
              <b data-to="100">0</b><span className="acount-pct">%</span>
              <span>Attention to detail</span>
            </div>
          </div>
        </div>
      </div>
    </section>
  );
}