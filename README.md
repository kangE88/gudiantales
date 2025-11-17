// useTabSwipe.ts
export function useTabSwipe(
  targetRef: Ref<HTMLElement | undefined>,
  activeTabRef: Ref<number>,
  tabCount: number,
  options?: UseTabSwipeOptions
) {
  const RESISTANCE_FACTOR = 0.1;
  const TRANSITION_DURATION = 350;
  const TRANSITION_EASING = 'cubic-bezier(0.4, 0, 0.2, 1)';
  
  const swipeOffset = ref(0);
  const isSwiping = ref(false);
  
  // 스타일 설정을 한 번만 수행 (초기화)
  const initializePanels = () => {
    const container = swipeTargetRef.value;
    if (!container) return;
    
    const panels = container.querySelectorAll('.sv-tabs__panel');
    if (panels.length === 0) return;
    
    // 컨테이너 설정
    Object.assign(container.style, {
      position: 'relative',
      overflow: 'hidden',
      touchAction: 'pan-y'  // 한 곳에서 처리
    });
    
    // 각 패널 초기 설정 (한 번만)
    panels.forEach((panel: Element, index: number) => {
      const htmlPanel = panel as HTMLElement;
      Object.assign(htmlPanel.style, {
        display: 'block',
        position: 'absolute',
        top: '0',
        left: '0',
        width: '100%',
        willChange: 'transform'  // 성능 최적화
      });
      
      // 데이터 속성으로 인덱스 저장 (스타일보다 의미론적)
      htmlPanel.dataset.panelIndex = String(index);
    });
  };
  
  // transform만 업데이트 (최소한의 DOM 조작)
  const updateTransforms = (offset: number, transitioning: boolean) => {
    const container = swipeTargetRef.value;
    if (!container) return;
    
    const panels = container.querySelectorAll('.sv-tabs__panel');
    const containerWidth = container.offsetWidth;
    const offsetPercent = (offset / containerWidth) * 100;
    
    panels.forEach((panel: Element) => {
      const htmlPanel = panel as HTMLElement;
      const index = Number(htmlPanel.dataset.panelIndex || 0);
      
      const baseTransform = (index - activeTabRef.value) * 100;
      const finalTransform = baseTransform + offsetPercent;
      
      // transition만 토글
      htmlPanel.style.transition = transitioning 
        ? `transform ${TRANSITION_DURATION}ms ${TRANSITION_EASING}`
        : 'none';
      
      htmlPanel.style.transform = `translateX(${finalTransform}%)`;
    });
  };
  
  // 이벤트 핸들러들...
  usePointerSwipe(swipeTargetRef, {
    threshold,
    onSwipeStart() {
      isSwiping.value = true;
      swipeOffset.value = 0;
    },
    onSwipe(e: PointerEvent) {
      if (!isSwiping.value) return;
      
      const distanceX = e.clientX - (e as any).startX;
      const isFirstTab = activeTabRef.value === 0;
      const isLastTab = activeTabRef.value === tabCount - 1;
      
      swipeOffset.value = 
        (isFirstTab && distanceX > 0) || (isLastTab && distanceX < 0)
          ? distanceX * RESISTANCE_FACTOR
          : distanceX;
      
      updateTransforms(swipeOffset.value, false);
    },
    onSwipeEnd(_e: PointerEvent, direction: UseSwipeDirection) {
      isSwiping.value = false;
      
      if (direction === "left" && activeTabRef.value < tabCount - 1) {
        navigateToNextTab();
      } else if (direction === "right" && activeTabRef.value > 0) {
        navigateToPrevTab();
      } else {
        updateTransforms(0, true);
      }
      
      swipeOffset.value = 0;
    }
  });
  
  // 초기화는 한 번만
  watchEffect(() => {
    const el = swipeTargetRef.value;
    if (el?.querySelector('.sv-tabs__panel')) {
      initializePanels();
      updateTransforms(0, false);
    }
  });
  
  // 탭 변경 시 애니메이션
  watch(activeTabRef, () => {
    if (!isSwiping.value) {
      updateTransforms(0, true);
    }
  });
  
  return { 
    navigateToNextTab, 
    navigateToPrevTab,
    swipeOffset: readonly(swipeOffset),
    isSwiping: readonly(isSwiping)
  };
}
