import imgLook1 from '../assets/lookbook/look-01.jpg';
import imgLook2 from '../assets/lookbook/look-02.jpg';
import imgLook3 from '../assets/lookbook/look-03.jpg';

const plates = [
  {
    cls: 'lg-a',
    speed: '0.06',
    img: imgLook1,
    alt: 'Side profile of model with textured wavy hair',
    num: '01',
    label: 'Textured Shag',
  },
  {
    cls: 'lg-b',
    speed: '0.13',
    img: imgLook2,
    alt: 'Front portrait of model with slick front hair',
    num: '02',
    label: 'Modern Slick Back',
  },
  {
    cls: 'lg-c',
    speed: '0.03',
    img: imgLook3,
    alt: 'Side profile of model with classic slicked hair in leather jacket',
    num: '03',
    label: 'Classic Pompadour',
  },
];

export default function Lookbook() {
  return (
    <section id="look">
      <div className="wrap">
        <div className="lhd">
          <div>
            <div className="lbl rv" style={{ marginBottom: 14, color: 'var(--mid)' }}>
              Lookbook
            </div>
            <h2 className="rv">Trending<br />Styles</h2>
          </div>
          <p className="rv">
            Trending cuts, refined for you. From textured crops and low tapers to modern fades and effortless slick-backs, we bring the latest men’s hair trends to the chair. Every style is tailored to your hair, face shape, and personal look—so you leave with a cut that feels distinctly yours.
          </p>
        </div>
        <div className="lgrid">
          {plates.map((p) => (
            <figure
              className={`${p.cls} rv`}
              data-scrub=""
              data-speed={p.speed}
              key={p.num}
            >
              <div className="ph">
                <img src={p.img} alt={p.alt} loading="lazy" />
              </div>
              <figcaption><span>{p.num}</span> {p.label}</figcaption>
            </figure>
          ))}
        </div>
      </div>
    </section>
  );
}
