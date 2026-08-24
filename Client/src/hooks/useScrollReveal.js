import { useEffect, useRef } from 'react';

/**
 * Attaches IntersectionObserver + fallback scroll sweep to reveal .rv elements.
 * Uses a MutationObserver to dynamically observe elements added to the DOM (like HMR).
 */
export default function useScrollReveal(deps = []) {
  const pending = useRef(new Set());
  const observerRef = useRef(null);

  useEffect(() => {
    const RM = window.matchMedia('(prefers-reduced-motion: reduce)').matches;

    const show = (el) => {
      el.classList.add('in');
      pending.current.delete(el);
      if (observerRef.current) observerRef.current.unobserve(el);
    };

    const io = new IntersectionObserver(
      (entries) => {
        entries.forEach((e) => {
          if (e.isIntersecting || e.boundingClientRect.top < 0) show(e.target);
        });
      },
      { threshold: 0.12, rootMargin: '0px 0px -6% 0px' }
    );
    observerRef.current = io;

    const registerNode = (el) => {
      if (el.classList.contains('in')) return;
      pending.current.add(el);
      if (RM) {
        show(el);
      } else {
        io.observe(el);
      }
    };

    // Observe initial nodes
    const initialNodes = document.querySelectorAll('.rv:not(.in)');
    initialNodes.forEach((node) => registerNode(node));

    // Stagger delay on initial batch
    initialNodes.forEach((el, i) => {
      el.style.transitionDelay = (i % 4) * 70 + 'ms';
    });

    /* fallback sweep for elements that jump past the observer */
    let sweeping = false;
    function sweep() {
      sweeping = false;
      [...pending.current].forEach((el) => {
        if (el.getBoundingClientRect().top < window.innerHeight * 0.94) show(el);
      });
    }
    function queue() {
      if (!sweeping) {
        sweeping = true;
        requestAnimationFrame(sweep);
      }
    }
    window.addEventListener('scroll', queue, { passive: true });
    window.addEventListener('load', sweep);

    // MutationObserver to watch for new .rv nodes added to DOM
    const mutationObserver = new MutationObserver((mutations) => {
      let addedRv = false;
      mutations.forEach((mutation) => {
        mutation.addedNodes.forEach((node) => {
          if (node.nodeType === Node.ELEMENT_NODE) {
            if (node.classList && node.classList.contains('rv')) {
              registerNode(node);
              addedRv = true;
            }
            node.querySelectorAll('.rv').forEach((el) => {
              registerNode(el);
              addedRv = true;
            });
          }
        });
      });
      if (addedRv) {
        sweep();
      }
    });

    mutationObserver.observe(document.body, {
      childList: true,
      subtree: true,
    });

    // Run initial sweep
    sweep();

    return () => {
      io.disconnect();
      mutationObserver.disconnect();
      window.removeEventListener('scroll', queue);
      window.removeEventListener('load', sweep);
    };
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, deps);
}
