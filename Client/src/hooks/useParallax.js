import { useEffect } from 'react';

/**
 * Single rAF-batched scroll pass driving:
 *  – the nav progress bar (#prog)
 *  – per-element parallax via [data-speed]
 *  – lookbook scrub via [data-scrub] (sets --p custom property)
 *  – hero model / wordmark parallax
 *  – season image parallax
 */
export default function useParallax() {
  useEffect(() => {
    const RM = window.matchMedia('(prefers-reduced-motion: reduce)').matches;
    if (RM) return;

    /* create the progress bar */
    const nav = document.querySelector('nav');
    let bar = document.getElementById('prog');
    if (!bar && nav) {
      bar = document.createElement('i');
      bar.id = 'prog';
      nav.appendChild(bar);
    }

    const speeds = () => [...document.querySelectorAll('[data-speed]')];
    const scrubs = () => [...document.querySelectorAll('[data-scrub]')];

    let q = false;

    function pass() {
      q = false;
      const h = document.documentElement.scrollHeight - window.innerHeight;
      if (bar) bar.style.transform = `scaleX(${h > 0 ? window.scrollY / h : 0})`;

      /* hero parallax */
      const y = window.scrollY;
      if (y < window.innerHeight * 1.2) {
        const model = document.getElementById('hm');
        const back = document.querySelector('#hero .word.back');
        if (model)
          model.style.transform = `translateY(${y * -0.06}px) scale(${1 + y * 0.00006})`;
        if (back) back.style.transform = `translateY(${y * 0.14}px)`;
      }

      /* season image parallax */
      const si = document.getElementById('seasonImg');
      if (si) {
        const r = si.parentElement.getBoundingClientRect();
        if (r.bottom > 0 && r.top < window.innerHeight) {
          si.style.transform = `scale(1.12) translateY(${(r.top / window.innerHeight) * -26}px)`;
        }
      }

      /* data-speed elements */
      for (const el of speeds()) {
        const r = el.getBoundingClientRect();
        if (r.bottom < -200 || r.top > window.innerHeight + 200) continue;
        const c = (r.top + r.height / 2 - window.innerHeight / 2) / window.innerHeight;
        el.style.transform = `translate3d(0, ${c * -parseFloat(el.dataset.speed) * 100}px, 0)`;
      }

      /* data-scrub elements (lookbook) */
      for (const el of scrubs()) {
        const r = el.getBoundingClientRect();
        if (r.bottom < 0 || r.top > window.innerHeight) continue;
        const p = Math.max(
          0,
          Math.min(1, 1 - (r.top - window.innerHeight * 0.25) / (window.innerHeight * 0.6))
        );
        el.style.setProperty('--p', p.toFixed(3));
      }
    }

    function onScroll() {
      if (!q) {
        q = true;
        requestAnimationFrame(pass);
      }
    }

    window.addEventListener('scroll', onScroll, { passive: true });
    window.addEventListener('resize', pass);
    pass();

    return () => {
      window.removeEventListener('scroll', onScroll);
      window.removeEventListener('resize', pass);
      if (bar && bar.parentNode) bar.parentNode.removeChild(bar);
    };
  }, []);
}
