

import { useThrottleFn, useWindowScroll, useWindowSize } from "@vueuse/core";



function one { 
    // Scroll handling for bottom action container
const checkScrolled = useThrottleFn(() => {
  const documentHeight = document.documentElement.scrollHeight;
  const scrollThreshold = 10;
  const isAtBottom = scrollY.value + windowHeight.value >= documentHeight - scrollThreshold;

  // 더 스크롤 내릴 수 있으면 addClass (scrolled = true)
  // 젤 아래로 내리면 removeClass (scrolled = false)
  isScrolled.value = !isAtBottom;
}, 10);
}

function two { 
    
}

function setupBottomActionHeightObserver(root: HTMLElement | Document) {
  try {
    const scopeEl: Document | HTMLElement = root instanceof HTMLElement ? root : document;

    // root가 곧바로 .sc-container 인 경우 자체를 컨테이너로 사용

    const container =
      root instanceof HTMLElement && root.classList.contains("sc-container")
        ? root
        : (scopeEl.querySelector(".sc-container") as HTMLElement | null);

    const bac = scopeEl.querySelector(".sv-bottom-action-container") as HTMLElement | null;

    if (!container) return;

    const applyHeight = () => {
      const target = (root instanceof HTMLElement ? root : document).querySelector(
        ".sv-bottom-action-container"
      ) as HTMLElement | null;

      const h = target ? Math.ceil(target.getBoundingClientRect().height) : 0;

      if (h > 0) {
        // px -> rem 변환 (루트 폰트 사이즈 기준)

        const rootFontSize = parseFloat(getComputedStyle(document.documentElement).fontSize) || 16;

        const remVal = h / rootFontSize;

        container.style.setProperty("--sc-bottom-action-height", `${remVal}px`);
      } else {
        container.style.removeProperty("--sc-bottom-action-height");
      }
    };

    // 초기 적용

    applyHeight();

    // ResizeObserver로 높이 변화를 추적

    if ("ResizeObserver" in window) {
      if (!bacResizeObserver) {
        bacResizeObserver = new ResizeObserver(() => applyHeight());
      }

      if (bac) bacResizeObserver.observe(bac);
    }

    // MutationObserver로 DOM 추가/제거도 추적하여 observer 재연결

    const mo = new MutationObserver(() => {
      applyHeight();

      const currentBac = scopeEl.querySelector(".sv-bottom-action-container") as HTMLElement | null;

      if (bacResizeObserver) {
        try {
          bacResizeObserver.disconnect();
        } catch {}

        if (currentBac) bacResizeObserver.observe(currentBac);
      }
    });

    mo.observe(container, { childList: true, subtree: true });
  } catch {}
}
