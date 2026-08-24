import imgOuterwear from '../assets/cat-taper-fade.jpg';
import imgKnitwear from '../assets/cat-textured-crop.jpg';
import imgTailoring from '../assets/cat-slick-back.jpg';

const categories = [
  {
    img: imgOuterwear,
    alt: 'Outerwear',
    title: 'LOW TAPER FADE',
    desc: 'Clean around the edges. Natural on top. A timeless modern cut.',
    label: 'DISCOVER THE LOOK',
  },
  {
    img: imgKnitwear,
    alt: 'Knitwear',
    title: 'TEXTURED CROP',
    desc: 'Sharp texture, effortless finish. Made for a modern, confident look.',
    label: 'DISCOVER THE LOOK',
  },
  {
    img: imgTailoring,
    alt: 'Tailoring',
    title: 'SLICK BACK',
    desc: 'Classic structure with a refined finish. Built for effortless sophistication.',
    label: 'DISCOVER THE LOOK',
  },
];

export default function Categories() {
  return (
    <section id="cats">
      <div className="wrap row">
        {categories.map((cat) => (
          <a href="#signup" className="rv" key={cat.title}>
            <div className="ph">
              <img src={cat.img} alt={cat.alt} loading="lazy" />
            </div>
            <div>
              <h3>{cat.title}</h3>
              <p>{cat.desc}</p>
              <span className="go">{cat.label} <i>→</i></span>
            </div>
          </a>
        ))}
      </div>
    </section>
  );
}
