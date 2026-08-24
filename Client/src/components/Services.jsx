const services = [
 { title: 'Expert Stylists', desc: 'Skilled barbers who understand your style.' },
{ title: 'Premium Experience', desc: 'Relaxed atmosphere, exceptional grooming.' },
{ title: 'Quality Products', desc: 'Professional products for a sharper finish.' },
{ title: 'Made For You', desc: 'Personalized cuts and grooming for every look.' },
];

export default function Services() {
  return (
    <section id="svc">
      <div className="wrap row">
        {services.map((s) => (
          <div className="rv" key={s.title}>
            <h4>{s.title}</h4>
            <p>{s.desc}</p>
          </div>
        ))}
      </div>
    </section>
  );
}
