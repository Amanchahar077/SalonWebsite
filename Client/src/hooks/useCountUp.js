import { useEffect } from 'react';

/**
 * Animated count-up for the atelier stats.
 * Watches #atelier .acount b[data-to] elements and triggers once on view.
 */
export default function useCountUp() {
  useEffect(() => {
    const RM = window.matchMedia('(prefers-reduced-motion: reduce)').matches;
    const nums = [...document.querySelectorAll('#atelier .acount b[data-to]')];
    if (!nums.length) return;

    if (RM) {
      nums.forEach((n) => (n.textContent = n.dataset.to));
      return;
    }

    const run = (n) => {
      const to = +n.dataset.to;
      const t0 = performance.now();
      const D = 1100;
      (function step(now) {
        const p = Math.min(1, (now - t0) / D);
        const e = 1 - Math.pow(1 - p, 3);
        n.textContent = Math.round(to * e);
        if (p < 1) requestAnimationFrame(step);
      })(t0);
    };

    const o = new IntersectionObserver(
      (es) =>
        es.forEach((e) => {
          if (e.isIntersecting) {
            run(e.target);
            o.unobserve(e.target);
          }
        }),
      { threshold: 0.6 }
    );
    nums.forEach((n) => o.observe(n));

    return () => o.disconnect();
  }, []);
}
