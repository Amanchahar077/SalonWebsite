export default function Announcement() {
  const items = [
    'Mon - Sun: 9AM - 9PM',
    'Expert Stylists',
    'Premium Care',
    'Exclusive Access',
  ];

  const renderTicker = (prefix = '') =>
    items.flatMap((text, i) => [
      <span key={`${prefix}s-${i}`}>{text}</span>,
      <i key={`${prefix}i-${i}`}>/</i>,
    ]);

  return (
    <div className="ann" aria-hidden="true">
      <div className="row">
        <div className="tk">{renderTicker('a')}</div>
        {/* duplicate for seamless loop */}
        <div className="tk">{renderTicker('b')}</div>
      </div>
    </div>
  );
}

