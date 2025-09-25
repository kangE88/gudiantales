

<!-- ScSwiper -->
<!-- components/SCSwiper.vue - 단순화 버전 -->
<template>
  <div
    :class="[containerClasses, `sc-swiper-${swiperId}`]"
    :data-effect="props.effect"
  >
    <!-- Swiper 컨테이너 -->
    <swiper
      ref="swiperRef"
      :modules="modules"
      :pagination="paginationConfig"
      :navigation="navigationConfig"
      :scrollbar="scrollbarConfig"
      :autoplay="autoplayConfig"
      :loop="props.loop"
      :slidesPerView="adjustedSlidesPerView"
      :spaceBetween="adjustedSpaceBetween"
      :centeredSlides="adjustedCenteredSlides"
      :direction="props.direction"
      :speed="props.speed"
      :effect="adjustedEffect"
      :breakpoints="props.breakpoints"
      v-bind="effectProps"
      @swiper="onSwiperInit"
      @slideChange="onSlideChange"
      @click="onSlideClick"
    >
      <!-- 데이터 기반 슬라이드 -->
      <template v-if="props.slides?.length">
        <swiper-slide
          v-for="(slide, index) in props.slides"
          :key="slide.id || index"
        >
          <slot
            name="slide"
            :item="slide"
            :index="index"
          >
            <div class="default-slide">
              <h3 v-if="slide.title">{{ slide.title }}</h3>
              <p v-if="slide.description">{{ slide.description }}</p>
              <img
                v-if="slide.image"
                :src="slide.image"
                :alt="slide.title || `Slide ${index + 1}`"
              />
            </div>
          </slot>
        </swiper-slide>
      </template>

      <!-- 슬롯 기반 슬라이드 -->
      <template v-else>
        <slot />
      </template>
    </swiper>

    <!-- Navigation -->
    <div
      v-if="shouldShowNavigation"
      :class="props.direction === 'vertical' ? 'swiper-button-prev-vertical' : 'swiper-button-prev'"
    ></div>
    <div
      v-if="shouldShowNavigation"
      :class="props.direction === 'vertical' ? 'swiper-button-next-vertical' : 'swiper-button-next'"
    ></div>

    <!-- Pagination -->
    <div
      v-if="shouldShowPagination"
      :class="props.direction === 'vertical' ? 'swiper-pagination-vertical' : 'swiper-pagination'"
    ></div>

    <!-- Scrollbar -->
    <div
      v-if="shouldShowScrollbar"
      :class="props.direction === 'vertical' ? 'swiper-scrollbar-vertical' : 'swiper-scrollbar'"
    ></div>
  </div>
</template>

<script setup lang="ts">
import {
  Autoplay,
  EffectCards,
  EffectCoverflow,
  EffectCreative,
  EffectCube,
  EffectFade,
  EffectFlip,
  Navigation,
  Pagination,
  Scrollbar,
} from "swiper/modules";
import { Swiper, SwiperSlide } from "swiper/vue";
import { computed, markRaw, onMounted, onUnmounted, reactive, shallowRef } from "vue";

// CSS imports
import "swiper/css";
import "swiper/css/effect-cards";
import "swiper/css/effect-coverflow";
import "swiper/css/effect-creative";
import "swiper/css/effect-cube";
import "swiper/css/effect-fade";
import "swiper/css/effect-flip";
import "swiper/css/navigation";
import "swiper/css/pagination";
import "swiper/css/scrollbar";

// ============================================================================
// TYPES & INTERFACES
// ============================================================================
export interface ScSwiperProps {
  slides?: any[];
  pagination?: boolean | "bullets" | "fraction" | "progressbar" | object;
  paginationType?: "bullets" | "fraction" | "progressbar" | "custom";
  navigation?: boolean | object;
  scrollbar?: boolean | object;
  autoplay?: boolean | object;
  loop?: boolean;
  slidesPerView?: number | "auto";
  spaceBetween?: number;
  centeredSlides?: boolean;
  direction?: "horizontal" | "vertical";
  speed?: number;
  effect?: "slide" | "fade" | "cube" | "coverflow" | "flip" | "cards" | "creative" | "cylinder";
  breakpoints?: { [key: number]: any };
  swiperId?: string;
  // Variants (단순화)
  size?: "small" | "medium" | "large";
  theme?: "default" | "dark" | "light";
}

// ============================================================================
// UTILITIES
// ============================================================================
let idCounter = 0;
const generateId = () => `swiper-${++idCounter}-${Date.now()}`;

// Effect 설정 맵
const EFFECT_CONFIGS = {
  cube: {
    cubeEffect: {
      shadow: true,
      slideShadows: true,
      shadowOffset: 50,
      shadowScale: 0.94,
    },
  },
  fade: {
    fadeEffect: {
      crossFade: true,
    },
  },
  coverflow: {
    coverflowEffect: {
      rotate: 0,
      stretch: 0,
      depth: 100,
      modifier: 1,
      slideShadows: true,
    },
  },
  flip: {
    flipEffect: {
      slideShadows: true,
      limitRotation: true,
    },
  },
  cards: {
    cardsEffect: {
      slideShadows: true,
      perSlideOffset: 5,
      perSlideRotate: 30,
      rotate: true,
    },
  },
  creative: {
    creativeEffect: {
      prev: {
        shadow: true,
        translate: ["-120%", 0, -500],
        rotate: [0, 0, -90],
      },
      next: {
        shadow: true,
        translate: ["120%", 0, -500],
        rotate: [0, 0, 90],
      },
    },
  },
  cylinder: {
    coverflowEffect: {
      rotate: 120,
      stretch: -100,
      depth: 800,
      modifier: 5,
      slideShadows: true,
      scale: 0.6,
    },
  },
};

// 모듈 맵
const MODULE_MAP = {
  pagination: Pagination,
  navigation: Navigation,
  scrollbar: Scrollbar,
  autoplay: Autoplay,
  fade: EffectFade,
  cube: EffectCube,
  coverflow: EffectCoverflow,
  flip: EffectFlip,
  cards: EffectCards,
  creative: EffectCreative,
};

// ============================================================================
// COMPONENT SETUP
// ============================================================================
const props = withDefaults(defineProps<ScSwiperProps>(), {
  slides: () => [],
  pagination: true,
  navigation: true,
  scrollbar: false,
  autoplay: false,
  loop: false,
  slidesPerView: 1,
  spaceBetween: 0,
  centeredSlides: false,
  direction: "horizontal",
  speed: 300,
  effect: "slide",
  size: "medium",
  theme: "default",
});

const emit = defineEmits<{
  slideChange: [{ activeIndex: number }];
  init: [any];
  slideClick: [
    {
      index: number;
      slideData?: any;
      event: Event;
      isActiveSlide: boolean;
      clickType: "single" | "double";
    },
  ];
  slideDoubleClick: [
    {
      index: number;
      slideData?: any;
      event: Event;
    },
  ];
}>();

// Refs
const swiperRef = shallowRef<any>(null);

// Computed
const swiperId = computed(() => props.swiperId || generateId());

const containerClasses = computed(() => {
  const baseClass = "sc-swiper-container";
  const sizeClass = `sc-swiper--${props.size}`;
  const themeClass = `sc-swiper--${props.theme}`;
  const directionClass = props.direction === "vertical" ? "sc-swiper--vertical" : "";
  return [baseClass, sizeClass, themeClass, directionClass];
});

const shouldShowNavigation = computed(() => props.navigation !== false);
const shouldShowPagination = computed(() => props.pagination !== false);
const shouldShowScrollbar = computed(() => props.scrollbar !== false);

// 필요한 모듈들을 동적으로 계산
const modules = computed(() => {
  const moduleList = [];

  if (shouldShowPagination.value) moduleList.push(MODULE_MAP.pagination);
  if (shouldShowNavigation.value) moduleList.push(MODULE_MAP.navigation);
  if (shouldShowScrollbar.value) moduleList.push(MODULE_MAP.scrollbar);
  if (props.autoplay) moduleList.push(MODULE_MAP.autoplay);

  // Effect 모듈 추가
  if (props.effect !== "slide") {
    const effectModuleKey = props.effect === "cylinder" ? "coverflow" : props.effect;
    if (MODULE_MAP[effectModuleKey as keyof typeof MODULE_MAP]) {
      moduleList.push(MODULE_MAP[effectModuleKey as keyof typeof MODULE_MAP]);
    }
  }

  return markRaw(moduleList);
});

// 설정들을 간단하게 - string selector 사용으로 DOM 참조 문제 해결
const navigationConfig = computed(() => {
  if (!shouldShowNavigation.value) return false;

  const config = {
    prevEl: `.sc-swiper-${swiperId.value} .swiper-button-prev`,
    nextEl: `.sc-swiper-${swiperId.value} .swiper-button-next`,
  };

  // Vertical direction일 때 navigation 방향 조정
  if (props.direction === "vertical") {
    config.prevEl = `.sc-swiper-${swiperId.value} .swiper-button-prev-vertical`;
    config.nextEl = `.sc-swiper-${swiperId.value} .swiper-button-next-vertical`;
  }

  return typeof props.navigation === "object" ? { ...config, ...props.navigation } : config;
});

const paginationConfig = computed(() => {
  if (!shouldShowPagination.value) return false;

  const config = {
    el: `.sc-swiper-${swiperId.value} .swiper-pagination`,
    clickable: true,
    type:
      props.paginationType || (typeof props.pagination === "string" ? props.pagination : "bullets"),
  };

  // Vertical direction일 때 pagination 위치 조정
  if (props.direction === "vertical") {
    config.el = `.sc-swiper-${swiperId.value} .swiper-pagination-vertical`;
  }

  return typeof props.pagination === "object" ? { ...config, ...props.pagination } : config;
});

const scrollbarConfig = computed(() => {
  if (!shouldShowScrollbar.value) return false;

  const config = {
    el: `.sc-swiper-${swiperId.value} .swiper-scrollbar`,
    draggable: true,
  };

  // Vertical direction일 때 scrollbar 위치 조정
  if (props.direction === "vertical") {
    config.el = `.sc-swiper-${swiperId.value} .swiper-scrollbar-vertical`;
  }

  return typeof props.scrollbar === "object" ? { ...config, ...props.scrollbar } : config;
});

const autoplayConfig = computed(() => {
  if (!props.autoplay) return false;

  const config = {
    delay: 3000,
    disableOnInteraction: false,
  };

  return typeof props.autoplay === "object" ? { ...config, ...props.autoplay } : config;
});

// Effect에 따른 slidesPerView 조정
const adjustedSlidesPerView = computed(() => {
  // Vertical direction일 때 특정 effects는 지원하지 않음
  if (props.direction === "vertical") {
    // Vertical에서는 slide와 fade만 지원
    if (!["slide", "fade"].includes(props.effect || "")) {
      console.warn(
        `Vertical direction doesn't support ${props.effect} effect. Falling back to slide effect.`
      );
      return 1;
    }
  }

  // Cube, Fade, Flip, Cards, Creative effect는 slidesPerView가 1이어야 함
  if (["cube", "fade", "flip", "cards", "creative"].includes(props.effect || "")) {
    return 1;
  }
  // Cylinder effect는 3개가 보이도록 설정
  if (props.effect === "cylinder") {
    return 3;
  }
  return props.slidesPerView;
});

// Effect별 spaceBetween 조정
const adjustedSpaceBetween = computed(() => {
  if (props.effect === "cylinder") {
    return 0;
  }
  return props.spaceBetween;
});

// Effect별 centeredSlides 조정
const adjustedCenteredSlides = computed(() => {
  if (props.effect === "cylinder") {
    return true;
  }
  return props.centeredSlides;
});

// Effect 이름 조정 (cylinder는 coverflow로 변환, vertical direction 제한)
const adjustedEffect = computed(() => {
  // Vertical direction일 때 slide와 fade만 지원
  if (props.direction === "vertical") {
    if (!["slide", "fade"].includes(props.effect || "")) {
      return "slide";
    }
  }
  return props.effect === "cylinder" ? "coverflow" : props.effect;
});

// Effect별 props를 동적으로 생성
const effectProps = computed(() => {
  if (props.effect === "slide") return {};

  // cylinder effect는 coverflow 설정을 사용
  const effectKey = props.effect === "cylinder" ? "cylinder" : props.effect;
  return EFFECT_CONFIGS[effectKey as keyof typeof EFFECT_CONFIGS] || {};
});

// ============================================================================
// EVENT HANDLERS
// ============================================================================
const onSwiperInit = (swiper: any) => {
  emit("init", swiper);

  // Vertical direction일 때 navigation 버튼 위치 강제 조정
  if (props.direction === "vertical") {
    setTimeout(adjustVerticalNavigationButtons, 100);
  }
};

const onSlideChange = (swiper: any) => {
  emit("slideChange", { activeIndex: swiper.activeIndex });
};

// 클릭 관련 상태 관리
const clickState = reactive({
  lastClickTime: 0,
  lastClickIndex: -1,
  clickTimeout: null as number | null,
});

const onSlideClick = (swiper: any, event: Event) => {
  const clickedSlide = (event.target as HTMLElement).closest(".swiper-slide");
  if (!clickedSlide) return;

  const slides = Array.from(swiper.slides);
  const index = slides.indexOf(clickedSlide);
  const slideData = props.slides?.[index];
  const isActiveSlide = swiper.activeIndex === index;
  const currentTime = Date.now();

  // 더블클릭 감지 (300ms 내 같은 슬라이드 클릭)
  const isDoubleClick =
    currentTime - clickState.lastClickTime < 300 && clickState.lastClickIndex === index;

  // 이전 클릭 타임아웃 클리어
  if (clickState.clickTimeout !== null) {
    clearTimeout(clickState.clickTimeout);
    clickState.clickTimeout = null;
  }

  if (isDoubleClick) {
    // 더블클릭 이벤트 발생
    emit("slideDoubleClick", {
      index,
      slideData,
      event,
    });

    // 더블클릭 후 상태 리셋
    clickState.lastClickTime = 0;
    clickState.lastClickIndex = -1;
  } else {
    // 싱글클릭 처리 (더블클릭 가능성을 위해 지연)
    clickState.clickTimeout = setTimeout(() => {
      emit("slideClick", {
        index,
        slideData,
        event,
        isActiveSlide,
        clickType: "single",
      });
      clickState.clickTimeout = null;
    }, 50); // 짧은 지연으로 더블클릭 감지 시간 확보
  }

  // 클릭 상태 업데이트
  clickState.lastClickTime = currentTime;
  clickState.lastClickIndex = index;
};

// ============================================================================
// LIFECYCLE
// ============================================================================
const adjustVerticalNavigationButtons = () => {
  if (props.direction === "vertical") {
    const prevButton = document.querySelector(
      `.sc-swiper-${swiperId.value} .swiper-button-prev-vertical`
    );
    const nextButton = document.querySelector(
      `.sc-swiper-${swiperId.value} .swiper-button-next-vertical`
    );

    const isMobile = window.innerWidth <= 768;
    const topPosition = isMobile ? "-50px" : "-60px";
    const bottomPosition = isMobile ? "-50px" : "-60px";

    if (prevButton) {
      (prevButton as HTMLElement).style.left = "50%";
      (prevButton as HTMLElement).style.top = topPosition;
      (prevButton as HTMLElement).style.right = "auto";
      (prevButton as HTMLElement).style.bottom = "auto";
      (prevButton as HTMLElement).style.transform = "translateX(-50%)";
      (prevButton as HTMLElement).style.display = "block";
      (prevButton as HTMLElement).style.visibility = "visible";
      (prevButton as HTMLElement).style.opacity = "1";
    }

    if (nextButton) {
      (nextButton as HTMLElement).style.left = "50%";
      (nextButton as HTMLElement).style.bottom = bottomPosition;
      (nextButton as HTMLElement).style.right = "auto";
      (nextButton as HTMLElement).style.top = "auto";
      (nextButton as HTMLElement).style.transform = "translateX(-50%)";
      (nextButton as HTMLElement).style.display = "block";
      (nextButton as HTMLElement).style.visibility = "visible";
      (nextButton as HTMLElement).style.opacity = "1";
    }
  }
};

onMounted(() => {
  // String selector 사용으로 DOM 참조 문제가 해결되어 별도 초기화 불필요

  // Vertical direction일 때 MutationObserver로 버튼 위치 지속적 조정
  if (props.direction === "vertical") {
    const container = document.querySelector(`.sc-swiper-${swiperId.value}`);
    if (container) {
      const observer = new MutationObserver(() => {
        adjustVerticalNavigationButtons();
      });

      observer.observe(container, {
        childList: true,
        subtree: true,
        attributes: true,
        attributeFilter: ["style", "class"],
      });

      // 윈도우 리사이즈 이벤트 추가
      const handleResize = () => {
        adjustVerticalNavigationButtons();
      };

      window.addEventListener("resize", handleResize);

      // 컴포넌트 언마운트 시 observer와 이벤트 리스너 정리
      onUnmounted(() => {
        observer.disconnect();
        window.removeEventListener("resize", handleResize);
      });
    }

    // 초기 조정
    setTimeout(adjustVerticalNavigationButtons, 100);
  }
});

// 컴포넌트 언마운트 시 타임아웃 정리
onUnmounted(() => {
  if (clickState.clickTimeout !== null) {
    clearTimeout(clickState.clickTimeout);
    clickState.clickTimeout = null;
  }
});

// ============================================================================
// EXPOSE
// ============================================================================
defineExpose({
  swiper: computed(() => swiperRef.value?.swiper),
  slideTo: (index: number) => swiperRef.value?.swiper?.slideTo(index),
  slideNext: () => swiperRef.value?.swiper?.slideNext(),
  slidePrev: () => swiperRef.value?.swiper?.slidePrev(),
  update: () => swiperRef.value?.swiper?.update(),
});
</script>

<style scoped>
/* ============================================================================
   기본 스타일
   ============================================================================ */
.sc-swiper-container {
  position: relative;
  width: 100%;
}

.sc-swiper-container .swiper {
  width: 100%;
  height: 100%;
}

/* ============================================================================
   Vertical Direction 스타일
   ============================================================================ */
.sc-swiper--vertical {
  height: 500px; /* 기본 높이 설정 */
  padding: 60px 0; /* 상하 패딩으로 navigation 버튼 공간 확보 */
}

.sc-swiper--vertical .swiper {
  height: 100%;
}

/* Vertical Navigation 버튼 스타일 - 더 강력한 선택자 사용 */
.sc-swiper--vertical :deep(.swiper-button-prev-vertical),
.sc-swiper--vertical :deep(.swiper-button-next-vertical) {
  color: #007aff !important;
  background: rgba(255, 255, 255, 0.9) !important;
  width: 44px !important;
  height: 44px !important;
  border-radius: 50% !important;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15) !important;
  transition: all 0.3s ease !important;
  z-index: 10 !important;
  margin: 0 !important;
  position: absolute !important;
}

/* Swiper 기본 스타일 완전 덮어쓰기 */
.sc-swiper--vertical :deep(.swiper-button-prev-vertical.swiper-button-disabled),
.sc-swiper--vertical :deep(.swiper-button-next-vertical.swiper-button-disabled) {
  opacity: 0.3 !important;
}

/* 전역 스타일로 Swiper 기본 위치 완전 덮어쓰기 */
:deep(.swiper-button-prev-vertical) {
  left: 50% !important;
  top: -60px !important;
  right: auto !important;
  bottom: auto !important;
  transform: translateX(-50%) !important;
  display: block !important;
  visibility: visible !important;
  opacity: 1 !important;
}

:deep(.swiper-button-next-vertical) {
  left: 50% !important;
  bottom: -60px !important;
  right: auto !important;
  top: auto !important;
  transform: translateX(-50%) !important;
  display: block !important;
  visibility: visible !important;
  opacity: 1 !important;
}

.sc-swiper--vertical :deep(.swiper-button-prev-vertical) {
  left: 50% !important;
  top: -60px !important;
  right: auto !important;
  bottom: auto !important;
  transform: translateX(-50%) !important;
}

.sc-swiper--vertical :deep(.swiper-button-next-vertical) {
  left: 50% !important;
  bottom: -60px !important;
  right: auto !important;
  top: auto !important;
  transform: translateX(-50%) !important;
}

.sc-swiper--vertical :deep(.swiper-button-prev-vertical:hover),
.sc-swiper--vertical :deep(.swiper-button-next-vertical:hover) {
  background: rgba(255, 255, 255, 1) !important;
  transform: translateX(-50%) scale(1.1) !important;
}

.sc-swiper--vertical :deep(.swiper-button-prev-vertical::after),
.sc-swiper--vertical :deep(.swiper-button-next-vertical::after) {
  font-size: 16px !important;
  font-weight: bold !important;
}

.sc-swiper--vertical :deep(.swiper-button-prev-vertical::after) {
  content: "↑" !important;
}

.sc-swiper--vertical :deep(.swiper-button-next-vertical::after) {
  content: "↓" !important;
}

/* Vertical Pagination 스타일 */
:deep(.swiper-pagination-vertical) {
  position: absolute !important;
  right: 10px !important;
  top: 50% !important;
  transform: translateY(-50%) !important;
  width: auto !important;
  height: auto !important;
  z-index: 10 !important;
}

:deep(.swiper-pagination-vertical .swiper-pagination-bullet) {
  display: block !important;
  margin: 8px 0 !important;
  background: rgba(0, 0, 0, 0.3) !important;
  opacity: 1 !important;
  transition: all 0.3s ease !important;
}

:deep(.swiper-pagination-vertical .swiper-pagination-bullet-active) {
  background: #007aff !important;
  transform: scale(1.2) !important;
}

/* Vertical Scrollbar 스타일 */
:deep(.swiper-scrollbar-vertical) {
  position: absolute !important;
  right: 3px !important;
  top: 1% !important;
  z-index: 50 !important;
  width: 5px !important;
  height: 98% !important;
}

:deep(.swiper-scrollbar-vertical .swiper-scrollbar-drag) {
  background: rgba(0, 0, 0, 0.3) !important;
  border-radius: 10px !important;
  position: relative !important;
  left: 0 !important;
  top: 0 !important;
}

/* ============================================================================
   기본 슬라이드 스타일 (공통 컴포넌트용)
   ============================================================================ */
.default-slide {
  padding: 20px;
  text-align: center;
  background: #f8f9fa;
  border-radius: 8px;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.default-slide h3 {
  margin: 0 0 12px 0;
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

.default-slide p {
  margin: 0 0 16px 0;
  font-size: 14px;
  color: #666;
  line-height: 1.5;
}

.default-slide img {
  max-width: 100%;
  height: auto;
  border-radius: 4px;
}

/* ============================================================================
   예제/데모용 슬라이드 스타일 (Swiper.vue와 공통)
   ============================================================================ */
:deep(.example-slide) {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 16px;
  position: relative;
  overflow: hidden;
  cursor: pointer;
  transition:
    transform 0.2s ease,
    box-shadow 0.2s ease;
}

:deep(.example-slide:hover) {
  transform: scale(1.02);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
}

:deep(.example-slide:active) {
  transform: scale(0.98);
}

:deep(.slide-content) {
  text-align: center;
  color: white;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.5);
  z-index: 2;
  padding: 20px;
}

:deep(.slide-content h3) {
  font-size: 2em;
  margin-bottom: 12px;
  font-weight: 600;
  color: white;
}

:deep(.slide-content p) {
  font-size: 1.2em;
  margin-bottom: 20px;
  opacity: 0.9;
  color: white;
}

:deep(.slide-number) {
  display: inline-block;
  background: rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(10px);
  padding: 8px 16px;
  border-radius: 20px;
  font-size: 0.9em;
  font-weight: 500;
  color: white;
}

:deep(.slide-content img) {
  max-width: 150px;
  max-height: 150px;
  border-radius: 8px;
  margin-top: 10px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);
}

/* ============================================================================
   Size Variants
   ============================================================================ */
.sc-swiper--small {
  font-size: 14px;
}

.sc-swiper--small .default-slide {
  padding: 15px;
  min-height: 200px;
}

.sc-swiper--medium {
  font-size: 16px;
}

.sc-swiper--medium .default-slide {
  padding: 20px;
  min-height: 300px;
}

.sc-swiper--large {
  font-size: 18px;
}

.sc-swiper--large .default-slide {
  padding: 30px;
  min-height: 400px;
}

/* ============================================================================
   Theme Variants
   ============================================================================ */
.sc-swiper--dark {
  background: #1a1a1a;
  color: white;
}

.sc-swiper--dark .default-slide {
  background: #2d2d2d;
  color: white;
}

.sc-swiper--minimal .default-slide {
  background: transparent;
  border: 1px solid #e0e0e0;
}

/* ============================================================================
   Navigation 스타일
   ============================================================================ */
:deep(.swiper-button-next),
:deep(.swiper-button-prev) {
  color: #007aff !important;
  background: rgba(255, 255, 255, 0.9) !important;
  width: 44px !important;
  height: 44px !important;
  border-radius: 50% !important;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15) !important;
  transition: all 0.3s ease !important;
  z-index: 10 !important;
  margin-top: -22px !important;
}

:deep(.swiper-button-next:hover),
:deep(.swiper-button-prev:hover) {
  background: rgba(255, 255, 255, 1) !important;
  transform: scale(1.1) !important;
}

:deep(.swiper-button-next::after),
:deep(.swiper-button-prev::after) {
  font-size: 16px !important;
  font-weight: bold !important;
}

:deep(.swiper-button-disabled) {
  opacity: 0.3 !important;
}

/* ============================================================================
   Pagination 스타일
   ============================================================================ */
:deep(.swiper-pagination) {
  z-index: 10 !important;
  position: relative !important;
}

:deep(.swiper-pagination-bullet) {
  background: rgba(0, 0, 0, 0.3) !important;
  opacity: 1 !important;
  transition: all 0.3s ease !important;
}

:deep(.swiper-pagination-bullet-active) {
  background: #007aff !important;
  transform: scale(1.2) !important;
}

:deep(.swiper-pagination-fraction) {
  color: #333 !important;
  font-weight: 500 !important;
}

:deep(.swiper-pagination-progressbar) {
  background: rgba(0, 0, 0, 0.1) !important;
}

:deep(.swiper-pagination-progressbar .swiper-pagination-progressbar-fill) {
  background: #007aff !important;
}

/* ============================================================================
   Cylinder Effect 전용 스타일 - 원통형 회전 효과
   ============================================================================ */
.sc-swiper-container[data-effect="cylinder"] {
  perspective: 2500px;
  perspective-origin: center bottom;
  overflow: visible;
  min-height: 450px;
  padding: 80px 0 40px 0;
  position: relative;
}

.sc-swiper-container[data-effect="cylinder"] .swiper {
  overflow: visible;
  transform-style: preserve-3d;
  position: relative;
}

.sc-swiper-container[data-effect="cylinder"] .swiper-wrapper {
  transform-style: preserve-3d;
  overflow: visible;
  display: flex;
  align-items: center;
  transform-origin: center bottom;
}

/* Cylinder Effect 기본 슬라이드 스타일 */
.sc-swiper-container[data-effect="cylinder"] :deep(.swiper-slide) {
  border-radius: 16px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.3);
  overflow: visible;
  transform-style: preserve-3d;
  transition: all 0.8s cubic-bezier(0.25, 0.46, 0.45, 0.94);
  backface-visibility: visible;
  will-change: transform;
  /* 기본 원통 배치 - 뒤쪽 하단에 위치 */
  transform: scale(0.7) translateY(80px) translateZ(-300px) rotateY(45deg) rotateX(15deg);
  opacity: 0.3;
  filter: brightness(0.6) contrast(0.8);
}

/* 활성 슬라이드 (원통 위쪽으로 상승) */
.sc-swiper-container[data-effect="cylinder"] :deep(.swiper-slide-active) {
  z-index: 30;
  /* 원통 위로 상승하는 효과 */
  transform: scale(1.15) translateY(-60px) translateZ(150px) rotateY(0deg) rotateX(0deg);
  box-shadow: 0 50px 100px rgba(0, 0, 0, 0.4);
  border: 3px solid rgba(255, 255, 255, 0.6);
  filter: brightness(1.2) contrast(1.15) saturate(1.1);
  opacity: 1;
}

/* 활성 슬라이드 바로 이전 */
.sc-swiper-container[data-effect="cylinder"] :deep(.swiper-slide-prev) {
  z-index: 15;
  /* 원통 좌측 하단 */
  transform: scale(0.8) translateY(50px) translateZ(-100px) rotateY(60deg) rotateX(10deg)
    translateX(-40px);
  opacity: 0.6;
  filter: brightness(0.7) contrast(0.9);
  box-shadow: 0 25px 50px rgba(0, 0, 0, 0.4);
}

/* 활성 슬라이드 바로 다음 */
.sc-swiper-container[data-effect="cylinder"] :deep(.swiper-slide-next) {
  z-index: 15;
  /* 원통 우측 하단 */
  transform: scale(0.8) translateY(50px) translateZ(-100px) rotateY(-60deg) rotateX(10deg)
    translateX(40px);
  opacity: 0.6;
  filter: brightness(0.7) contrast(0.9);
  box-shadow: 0 25px 50px rgba(0, 0, 0, 0.4);
}

/* 더 멀리 있는 슬라이드들 */
.sc-swiper-container[data-effect="cylinder"] :deep(.swiper-slide-duplicate-prev),
.sc-swiper-container[data-effect="cylinder"] :deep(.swiper-slide-duplicate-next) {
  z-index: 5;
  transform: scale(0.6) translateY(100px) translateZ(-400px) rotateY(80deg) rotateX(20deg);
  opacity: 0.2;
  filter: brightness(0.4) contrast(0.7);
}

/* 기타 원통 뒤쪽 슬라이드들 */
.sc-swiper-container[data-effect="cylinder"]
  :deep(
    .swiper-slide:not(.swiper-slide-active):not(.swiper-slide-prev):not(.swiper-slide-next):not(
        .swiper-slide-duplicate-prev
      ):not(.swiper-slide-duplicate-next)
  ) {
  z-index: 1;
  transform: scale(0.65) translateY(90px) translateZ(-350px) rotateY(75deg) rotateX(18deg);
  opacity: 0.25;
  filter: brightness(0.5) contrast(0.75);
}

/* 원통 효과 강화 - 그라데이션 베이스 */
.sc-swiper-container[data-effect="cylinder"]::before {
  content: "";
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 80%;
  height: 30px;
  background: radial-gradient(ellipse at center, rgba(0, 0, 0, 0.3) 0%, transparent 70%);
  border-radius: 50%;
  z-index: 0;
}

/* 호버 효과 */
.sc-swiper-container[data-effect="cylinder"] :deep(.swiper-slide:hover) {
  transform: scale(1.05) translateY(-10px) !important;
  filter: brightness(1.1) contrast(1.05) !important;
  transition: all 0.3s ease !important;
}

/* ============================================================================
   Coverflow Effect 전용 스타일 - iTunes 스타일 강화
   ============================================================================ */
.sc-swiper-container[data-effect="coverflow"] {
  perspective: 1200px;
  perspective-origin: center center;
  overflow: visible;
  min-height: 400px;
  padding: 60px 50px;
  position: relative;
}

.sc-swiper-container[data-effect="coverflow"] .swiper {
  overflow: visible;
  transform-style: preserve-3d;
}

.sc-swiper-container[data-effect="coverflow"] .swiper-wrapper {
  transform-style: preserve-3d;
  overflow: visible;
  display: flex;
  align-items: center;
}

/* Coverflow 기본 슬라이드 스타일 */
.sc-swiper-container[data-effect="coverflow"] :deep(.swiper-slide) {
  border-radius: 12px;
  box-shadow: 0 15px 30px rgba(0, 0, 0, 0.2);
  overflow: hidden;
  transform-style: preserve-3d;
  transition: all 0.6s cubic-bezier(0.25, 0.46, 0.45, 0.94);
  backface-visibility: visible;
  will-change: transform;
  width: 280px !important;
  height: 350px;
  opacity: 0.6;
  filter: brightness(0.7) contrast(0.8);
}

/* 활성 슬라이드 (중앙 강조) */
.sc-swiper-container[data-effect="coverflow"] :deep(.swiper-slide-active) {
  z-index: 20;
  transform: scale(1.2) translateY(-20px) translateZ(100px) rotateY(0deg);
  box-shadow: 0 40px 80px rgba(0, 0, 0, 0.4);
  border: 3px solid rgba(255, 255, 255, 0.5);
  filter: brightness(1.1) contrast(1.1) saturate(1.2);
  opacity: 1;
}

/* 이전 슬라이드 (좌측 회전) */
.sc-swiper-container[data-effect="coverflow"] :deep(.swiper-slide-prev) {
  z-index: 10;
  transform: scale(0.85) translateY(10px) translateZ(-50px) rotateY(45deg) translateX(-30px);
  opacity: 0.7;
  filter: brightness(0.8) contrast(0.9);
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.3);
}

/* 다음 슬라이드 (우측 회전) */
.sc-swiper-container[data-effect="coverflow"] :deep(.swiper-slide-next) {
  z-index: 10;
  transform: scale(0.85) translateY(10px) translateZ(-50px) rotateY(-45deg) translateX(30px);
  opacity: 0.7;
  filter: brightness(0.8) contrast(0.9);
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.3);
}

/* 2번째 이전 슬라이드 */
.sc-swiper-container[data-effect="coverflow"] :deep(.swiper-slide-duplicate-prev) {
  z-index: 5;
  transform: scale(0.7) translateY(20px) translateZ(-100px) rotateY(60deg) translateX(-50px);
  opacity: 0.4;
  filter: brightness(0.6) contrast(0.8);
}

/* 2번째 다음 슬라이드 */
.sc-swiper-container[data-effect="coverflow"] :deep(.swiper-slide-duplicate-next) {
  z-index: 5;
  transform: scale(0.7) translateY(20px) translateZ(-100px) rotateY(-60deg) translateX(50px);
  opacity: 0.4;
  filter: brightness(0.6) contrast(0.8);
}

/* 기타 멀리 있는 슬라이드들 */
.sc-swiper-container[data-effect="coverflow"]
  :deep(
    .swiper-slide:not(.swiper-slide-active):not(.swiper-slide-prev):not(.swiper-slide-next):not(
        .swiper-slide-duplicate-prev
      ):not(.swiper-slide-duplicate-next)
  ) {
  z-index: 1;
  transform: scale(0.6) translateY(30px) translateZ(-150px) rotateY(75deg);
  opacity: 0.3;
  filter: brightness(0.5) contrast(0.7);
}

/* Coverflow 반사 효과 */
.sc-swiper-container[data-effect="coverflow"]::after {
  content: "";
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 90%;
  height: 80px;
  background: linear-gradient(
    to bottom,
    rgba(255, 255, 255, 0.1) 0%,
    rgba(255, 255, 255, 0.05) 50%,
    transparent 100%
  );
  border-radius: 50%;
  z-index: 0;
}

/* Coverflow 호버 효과 */
.sc-swiper-container[data-effect="coverflow"] :deep(.swiper-slide:hover) {
  transform: scale(1.05) translateY(-5px) !important;
  filter: brightness(1.1) contrast(1.05) !important;
  transition: all 0.3s ease !important;
}

/* ============================================================================
   반응형
   ============================================================================ */
@media (max-width: 768px) {
  :deep(.swiper-button-next),
  :deep(.swiper-button-prev) {
    width: 36px;
    height: 36px;
  }

  :deep(.swiper-button-next::after),
  :deep(.swiper-button-prev::after) {
    font-size: 14px;
  }

  /* Cylinder Effect 모바일 조정 */
  .sc-swiper-container[data-effect="cylinder"] {
    perspective: 1800px;
    perspective-origin: center bottom;
    min-height: 350px;
    padding: 60px 0 30px 0;
  }

  /* 모바일 활성 슬라이드 - 위로 더 명확하게 상승 */
  .sc-swiper-container[data-effect="cylinder"] :deep(.swiper-slide-active) {
    transform: scale(1.1) translateY(-40px) translateZ(120px) rotateY(0deg) rotateX(0deg);
    box-shadow: 0 40px 80px rgba(0, 0, 0, 0.4);
  }

  /* 모바일 이전 슬라이드 */
  .sc-swiper-container[data-effect="cylinder"] :deep(.swiper-slide-prev) {
    transform: scale(0.75) translateY(40px) translateZ(-80px) rotateY(50deg) rotateX(8deg)
      translateX(-30px);
    opacity: 0.5;
  }

  /* 모바일 다음 슬라이드 */
  .sc-swiper-container[data-effect="cylinder"] :deep(.swiper-slide-next) {
    transform: scale(0.75) translateY(40px) translateZ(-80px) rotateY(-50deg) rotateX(8deg)
      translateX(30px);
    opacity: 0.5;
  }

  /* 모바일 기타 슬라이드 */
  .sc-swiper-container[data-effect="cylinder"]
    :deep(.swiper-slide:not(.swiper-slide-active):not(.swiper-slide-prev):not(.swiper-slide-next)) {
    transform: scale(0.6) translateY(70px) translateZ(-250px) rotateY(65deg) rotateX(15deg);
    opacity: 0.2;
  }

  /* 모바일 그라데이션 베이스 조정 */
  .sc-swiper-container[data-effect="cylinder"]::before {
    width: 70%;
    height: 20px;
  }

  /* Coverflow Effect 모바일 조정 */
  .sc-swiper-container[data-effect="coverflow"] {
    perspective: 1000px;
    min-height: 320px;
    padding: 40px 20px;
  }

  /* 모바일 Coverflow 기본 슬라이드 */
  .sc-swiper-container[data-effect="coverflow"] :deep(.swiper-slide) {
    width: 220px !important;
    height: 280px;
  }

  /* 모바일 활성 슬라이드 */
  .sc-swiper-container[data-effect="coverflow"] :deep(.swiper-slide-active) {
    transform: scale(1.15) translateY(-15px) translateZ(80px) rotateY(0deg);
    box-shadow: 0 30px 60px rgba(0, 0, 0, 0.4);
  }

  /* 모바일 이전/다음 슬라이드 */
  .sc-swiper-container[data-effect="coverflow"] :deep(.swiper-slide-prev) {
    transform: scale(0.8) translateY(8px) translateZ(-40px) rotateY(35deg) translateX(-20px);
    opacity: 0.6;
  }

  .sc-swiper-container[data-effect="coverflow"] :deep(.swiper-slide-next) {
    transform: scale(0.8) translateY(8px) translateZ(-40px) rotateY(-35deg) translateX(20px);
    opacity: 0.6;
  }

  /* 모바일 기타 슬라이드 */
  .sc-swiper-container[data-effect="coverflow"]
    :deep(.swiper-slide:not(.swiper-slide-active):not(.swiper-slide-prev):not(.swiper-slide-next)) {
    transform: scale(0.65) translateY(20px) translateZ(-100px) rotateY(55deg);
    opacity: 0.3;
  }

  /* 모바일 반사 효과 조정 */
  .sc-swiper-container[data-effect="coverflow"]::after {
    width: 80%;
    height: 60px;
  }

  /* 모바일 예제 슬라이드 스타일 조정 */
  :deep(.slide-content h3) {
    font-size: 1.5em !important;
  }

  :deep(.slide-content p) {
    font-size: 1em !important;
  }

  :deep(.slide-content) {
    padding: 15px !important;
  }

  :deep(.slide-content img) {
    max-width: 120px !important;
    max-height: 120px !important;
  }

  /* Vertical Direction 모바일 조정 */
  .sc-swiper--vertical {
    height: 300px;
    padding: 50px 0; /* 모바일에서 패딩 조정 */
  }

  .sc-swiper--vertical :deep(.swiper-button-prev-vertical),
  .sc-swiper--vertical :deep(.swiper-button-next-vertical) {
    width: 36px !important;
    height: 36px !important;
  }

  .sc-swiper--vertical :deep(.swiper-button-prev-vertical) {
    top: -50px !important;
    left: 50% !important;
    right: auto !important;
    bottom: auto !important;
  }

  .sc-swiper--vertical :deep(.swiper-button-next-vertical) {
    bottom: -50px !important;
    left: 50% !important;
    right: auto !important;
    top: auto !important;
  }

  .sc-swiper--vertical :deep(.swiper-button-prev-vertical::after),
  .sc-swiper--vertical :deep(.swiper-button-next-vertical::after) {
    font-size: 14px !important;
  }

  :deep(.swiper-pagination-vertical) {
    right: 5px !important;
  }

  :deep(.swiper-pagination-vertical .swiper-pagination-bullet) {
    margin: 6px 0 !important;
  }
}
</style>


\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
StoryBook
\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

import ScSwiper from "./ScSwiper.vue";
import type { Meta, StoryObj } from "@storybook/vue3";

// 예시 슬라이드 데이터
const mockSlides = [
  {
    id: "slide-1",
    title: "Amazing Slide 1",
    subtitle: "Beautiful gradient background",
    description: "첫 번째 슬라이드입니다.",
    background: "linear-gradient(45deg, #667eea, #764ba2)",
    image: "https://picsum.photos/300/200?random=1",
  },
  {
    id: "slide-2",
    title: "Incredible Slide 2",
    subtitle: "Stunning visual effects",
    description: "두 번째 슬라이드입니다.",
    background: "linear-gradient(45deg, #f093fb, #f5576c)",
    image: "https://picsum.photos/300/200?random=2",
  },
  {
    id: "slide-3",
    title: "Awesome Slide 3",
    subtitle: "Modern design approach",
    description: "세 번째 슬라이드입니다.",
    background: "linear-gradient(45deg, #4facfe, #00f2fe)",
    image: "https://picsum.photos/300/200?random=3",
  },
  {
    id: "slide-4",
    title: "Fantastic Slide 4",
    subtitle: "Interactive experience",
    description: "네 번째 슬라이드입니다.",
    background: "linear-gradient(45deg, #fa709a, #fee140)",
    image: "https://picsum.photos/300/200?random=4",
  },
  {
    id: "slide-5",
    title: "Spectacular Slide 5",
    subtitle: "Premium quality content",
    description: "다섯 번째 슬라이드입니다.",
    background: "linear-gradient(45deg, #a8edea, #fed6e3)",
    image: "https://picsum.photos/300/200?random=5",
  },
];

const meta: Meta<typeof ScSwiper> = {
  title: "SHC/ScSwiper",
  component: ScSwiper,
  parameters: {
    layout: "padded",
    docs: {
      description: {
        component: `
# ScSwiper 컴포넌트

Swiper.js 기반의 고급 슬라이더 컴포넌트입니다. 8가지 시각적 효과와 다양한 옵션을 제공합니다.

## 주요 기능
- 🎯 **8가지 Effect**: Slide, Fade, Cube, Coverflow, Flip, Cards, Creative, Cylinder
- 🎨 **테마 지원**: Default, Dark, Light
- 📱 **반응형**: 모바일 최적화
- 🎮 **Navigation & Pagination**: 다양한 스타일 지원
- ⚡ **Autoplay**: 자동 재생 기능
- 🎪 **3D Effects**: 입체적인 시각 효과

## 내장 스타일
컴포넌트에 \`.example-slide\`, \`.slide-content\` 스타일이 내장되어 있어 별도 스타일링 없이 바로 사용 가능합니다.
        `,
      },
    },
  },
  args: {
    slides: mockSlides,
    slidesPerView: 1,
    spaceBetween: 16,
    pagination: true,
    paginationType: "bullets",
    navigation: true,
    autoplay: false,
    loop: false,
    centeredSlides: false,
    size: "medium",
    theme: "default",
    effect: "slide",
    speed: 300,
    direction: "horizontal",
  },
  argTypes: {
    slides: {
      description: "슬라이드 데이터 배열",
      table: {
        type: { summary: "Array<SlideData>" },
      },
    },
    effect: {
      control: "radio",
      options: ["slide", "fade", "cube", "coverflow", "flip", "cards", "creative", "cylinder"],
      description: "슬라이더 전환 효과",
      table: {
        type: {
          summary: `"slide" | "fade" | "cube" | "coverflow" | "flip" | "cards" | "creative" | "cylinder"`,
        },
        defaultValue: { summary: "slide" },
      },
    },
    size: {
      control: "radio",
      options: ["small", "medium", "large"],
      description: "슬라이더 크기",
      table: {
        type: { summary: `"small" | "medium" | "large"` },
        defaultValue: { summary: "medium" },
      },
    },
    theme: {
      control: "radio",
      options: ["default", "dark", "light"],
      description: "테마 스타일",
      table: {
        type: { summary: `"default" | "dark" | "light"` },
        defaultValue: { summary: "default" },
      },
    },
    paginationType: {
      control: "radio",
      options: ["bullets", "fraction", "progressbar", "custom"],
      description: "페이지네이션 타입",
      table: {
        type: { summary: `"bullets" | "fraction" | "progressbar" | "custom"` },
        defaultValue: { summary: "bullets" },
      },
    },
    slidesPerView: {
      control: { type: "number", min: 1, max: 5, step: 1 },
      description: "동시에 보여줄 슬라이드 수",
      table: {
        type: { summary: "number | 'auto'" },
        defaultValue: { summary: "1" },
      },
    },
    spaceBetween: {
      control: { type: "number", min: 0, max: 50, step: 4 },
      description: "슬라이드 간 간격(px)",
      table: {
        type: { summary: "number" },
        defaultValue: { summary: "0" },
      },
    },
    speed: {
      control: { type: "number", min: 100, max: 1000, step: 100 },
      description: "전환 애니메이션 속도(ms)",
      table: {
        type: { summary: "number" },
        defaultValue: { summary: "300" },
      },
    },
    direction: {
      control: "radio",
      options: ["horizontal", "vertical"],
      description: "슬라이드 방향",
      table: {
        type: { summary: `"horizontal" | "vertical"` },
        defaultValue: { summary: "horizontal" },
      },
    },
    pagination: {
      control: "boolean",
      description: "페이지네이션 표시 여부",
      table: {
        type: { summary: "boolean" },
        defaultValue: { summary: "true" },
      },
    },
    navigation: {
      control: "boolean",
      description: "좌우 네비게이션 버튼 표시 여부",
      table: {
        type: { summary: "boolean" },
        defaultValue: { summary: "true" },
      },
    },
    autoplay: {
      control: "boolean",
      description: "자동 재생 여부",
      table: {
        type: { summary: "boolean | object" },
        defaultValue: { summary: "false" },
      },
    },
    loop: {
      control: "boolean",
      description: "무한 루프 여부",
      table: {
        type: { summary: "boolean" },
        defaultValue: { summary: "false" },
      },
    },
    centeredSlides: {
      control: "boolean",
      description: "슬라이드 중앙 정렬 여부",
      table: {
        type: { summary: "boolean" },
        defaultValue: { summary: "false" },
      },
    },
  },
};

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  parameters: {
    docs: {
      description: {
        story:
          "기본 슬라이드 효과를 사용한 표준 구성입니다. 내장된 example-slide 스타일을 활용합니다.",
      },
    },
  },
  render: (args: any) => ({
    components: { ScSwiper },
    setup() {
      return { args };
    },
    template: `
      <ScSwiper v-bind="args">
        <template #slide="{ item, index }">
          <div class="example-slide" :style="{ background: item.background }">
            <div class="slide-content">
              <h3>{{ item.title }}</h3>
              <p>{{ item.subtitle }}</p>
              <span class="slide-number">{{ index + 1 }}</span>
            </div>
          </div>
        </template>
      </ScSwiper>
    `,
  }),
};

// 8가지 Effect 스토리들
export const SlideEffect: Story = {
  args: {
    effect: "slide",
    slidesPerView: 1,
    spaceBetween: 30,
  },
  parameters: {
    docs: {
      description: {
        story: "기본적인 좌우 슬라이딩 효과입니다.",
      },
    },
  },
  render: (args: any) => ({
    components: { ScSwiper },
    setup() {
      return { args };
    },
    template: `
      <ScSwiper v-bind="args">
        <template #slide="{ item, index }">
          <div class="example-slide" :style="{ background: item.background }">
            <div class="slide-content">
              <h3>{{ item.title }}</h3>
              <p>{{ item.subtitle }}</p>
              <span class="slide-number">{{ index + 1 }}</span>
            </div>
          </div>
        </template>
      </ScSwiper>
    `,
  }),
};

export const FadeEffect: Story = {
  args: {
    effect: "fade",
    autoplay: {
      delay: 3000,
      disableOnInteraction: false,
    },
    speed: 500,
    paginationType: "fraction",
  },
  parameters: {
    docs: {
      description: {
        story:
          "부드러운 페이드 인/아웃 전환 효과입니다. 자동 재생과 fraction 페이지네이션을 사용합니다.",
      },
    },
  },
  render: (args: any) => ({
    components: { ScSwiper },
    setup() {
      return { args };
    },
    template: `
      <ScSwiper v-bind="args">
        <template #slide="{ item, index }">
          <div class="example-slide" :style="{ background: item.background }">
            <div class="slide-content">
              <h3>{{ item.title }}</h3>
              <p>{{ item.subtitle }}</p>
              <span class="slide-number">{{ index + 1 }}</span>
            </div>
          </div>
        </template>
      </ScSwiper>
    `,
  }),
};

export const CubeEffect: Story = {
  args: {
    effect: "cube",
    speed: 600,
    paginationType: "bullets",
  },
  parameters: {
    docs: {
      description: {
        story: "3D 큐브 회전 효과입니다. 입체적인 회전 애니메이션을 제공합니다.",
      },
    },
  },
  render: (args: any) => ({
    components: { ScSwiper },
    setup() {
      return { args };
    },
    template: `
      <ScSwiper v-bind="args">
        <template #slide="{ item, index }">
          <div class="example-slide" :style="{ background: item.background }">
            <div class="slide-content">
              <h3>{{ item.title }}</h3>
              <p>{{ item.subtitle }}</p>
              <span class="slide-number">{{ index + 1 }}</span>
            </div>
          </div>
        </template>
      </ScSwiper>
    `,
  }),
};

export const CoverflowEffect: Story = {
  args: {
    effect: "coverflow",
    slidesPerView: "auto",
    spaceBetween: 30,
    centeredSlides: true,
    paginationType: "progressbar",
    speed: 400,
  },
  parameters: {
    docs: {
      description: {
        story: "iTunes 스타일의 3D 커버플로우 효과입니다. 활성 슬라이드가 강조됩니다.",
      },
    },
  },
  render: (args: any) => ({
    components: { ScSwiper },
    setup() {
      return { args };
    },
    template: `
      <ScSwiper v-bind="args">
        <template #slide="{ item, index }">
          <div class="example-slide" :style="{ background: item.background }">
            <div class="slide-content">
              <h3>{{ item.title }}</h3>
              <p>{{ item.subtitle }}</p>
              <span class="slide-number">{{ index + 1 }}</span>
            </div>
          </div>
        </template>
      </ScSwiper>
    `,
  }),
};

export const FlipEffect: Story = {
  args: {
    effect: "flip",
    speed: 600,
    paginationType: "bullets",
  },
  parameters: {
    docs: {
      description: {
        story: "카드 뒤집기 효과입니다. X축 기준 회전 애니메이션을 제공합니다.",
      },
    },
  },
  render: (args: any) => ({
    components: { ScSwiper },
    setup() {
      return { args };
    },
    template: `
      <ScSwiper v-bind="args">
        <template #slide="{ item, index }">
          <div class="example-slide" :style="{ background: item.background }">
            <div class="slide-content">
              <h3>{{ item.title }}</h3>
              <p>{{ item.subtitle }}</p>
              <span class="slide-number">{{ index + 1 }}</span>
            </div>
          </div>
        </template>
      </ScSwiper>
    `,
  }),
};

export const CardsEffect: Story = {
  args: {
    effect: "cards",
    speed: 400,
    paginationType: "custom",
  },
  parameters: {
    docs: {
      description: {
        story: "카드 스택 효과입니다. 카드가 쌓인 형태로 전환됩니다.",
      },
    },
  },
  render: (args: any) => ({
    components: { ScSwiper },
    setup() {
      return { args };
    },
    template: `
      <ScSwiper v-bind="args">
        <template #slide="{ item, index }">
          <div class="example-slide" :style="{ background: item.background }">
            <div class="slide-content">
              <h3>{{ item.title }}</h3>
              <p>{{ item.subtitle }}</p>
              <span class="slide-number">{{ index + 1 }}</span>
            </div>
          </div>
        </template>
      </ScSwiper>
    `,
  }),
};

export const CreativeEffect: Story = {
  args: {
    effect: "creative",
    speed: 700,
    paginationType: "fraction",
  },
  parameters: {
    docs: {
      description: {
        story: "창의적인 3D 전환 효과입니다. 커스텀 애니메이션을 제공합니다.",
      },
    },
  },
  render: (args: any) => ({
    components: { ScSwiper },
    setup() {
      return { args };
    },
    template: `
      <ScSwiper v-bind="args">
        <template #slide="{ item, index }">
          <div class="example-slide" :style="{ background: item.background }">
            <div class="slide-content">
              <h3>{{ item.title }}</h3>
              <p>{{ item.subtitle }}</p>
              <span class="slide-number">{{ index + 1 }}</span>
            </div>
          </div>
        </template>
      </ScSwiper>
    `,
  }),
};

export const CylinderEffect: Story = {
  args: {
    effect: "cylinder",
    slidesPerView: 3,
    spaceBetween: 0,
    centeredSlides: true,
    speed: 800,
    paginationType: "bullets",
  },
  parameters: {
    docs: {
      description: {
        story:
          "원통형 3D 회전 효과입니다. 활성 슬라이드가 원통 위로 상승하는 독특한 효과를 제공합니다.",
      },
    },
  },
  render: (args: any) => ({
    components: { ScSwiper },
    setup() {
      return { args };
    },
    template: `
      <ScSwiper v-bind="args">
        <template #slide="{ item, index }">
          <div class="example-slide" :style="{ background: item.background }">
            <div class="slide-content">
              <h3>{{ item.title }}</h3>
              <p>{{ item.subtitle }}</p>
              <span class="slide-number">{{ index + 1 }}</span>
            </div>
          </div>
        </template>
      </ScSwiper>
    `,
  }),
};

// 테마 & 크기 스토리들
export const DarkTheme: Story = {
  args: {
    theme: "dark",
    size: "large",
    autoplay: {
      delay: 2500,
      disableOnInteraction: false,
    },
  },
  parameters: {
    docs: {
      description: {
        story: "다크 테마 적용 예시입니다.",
      },
    },
  },
  render: (args: any) => ({
    components: { ScSwiper },
    setup() {
      return { args };
    },
    template: `
      <ScSwiper v-bind="args">
        <template #slide="{ item, index }">
          <div class="example-slide" :style="{ background: item.background }">
            <div class="slide-content">
              <h3>{{ item.title }}</h3>
              <p>{{ item.subtitle }}</p>
              <span class="slide-number">{{ index + 1 }}</span>
            </div>
          </div>
        </template>
      </ScSwiper>
    `,
  }),
};

export const MultipleSlides: Story = {
  args: {
    slidesPerView: 3,
    spaceBetween: 16,
    centeredSlides: false,
    size: "medium",
  },
  parameters: {
    docs: {
      description: {
        story: "여러 슬라이드를 동시에 보여주는 예시입니다.",
      },
    },
  },
  render: (args: any) => ({
    components: { ScSwiper },
    setup() {
      return { args };
    },
    template: `
      <ScSwiper v-bind="args">
        <template #slide="{ item, index }">
          <div class="example-slide" :style="{ background: item.background }">
            <div class="slide-content">
              <h3>{{ item.title }}</h3>
              <p>{{ item.subtitle }}</p>
              <span class="slide-number">{{ index + 1 }}</span>
            </div>
          </div>
        </template>
      </ScSwiper>
    `,
  }),
};

export const WithClickEvents: Story = {
  args: {
    effect: "slide",
    slidesPerView: 1,
    spaceBetween: 20,
    pagination: true,
    navigation: true,
  },
  parameters: {
    docs: {
      description: {
        story:
          "슬라이드 클릭 이벤트를 테스트할 수 있는 예시입니다. 싱글클릭과 더블클릭을 모두 지원합니다.",
      },
    },
  },
  render: (args: any) => ({
    components: { ScSwiper },
    setup() {
      const handleSlideClick = (event: any) => {
        console.log("슬라이드 클릭:", {
          index: event.index,
          title: event.slideData?.title,
          isActiveSlide: event.isActiveSlide,
          clickType: event.clickType,
        });
        alert(`슬라이드 ${event.index + 1} 클릭! (${event.clickType})`);
      };

      const handleSlideDoubleClick = (event: any) => {
        console.log("슬라이드 더블클릭:", {
          index: event.index,
          title: event.slideData?.title,
        });
        alert(`슬라이드 ${event.index + 1} 더블클릭!`);
      };

      return { args, handleSlideClick, handleSlideDoubleClick };
    },
    template: `
      <div>
        <p style="margin-bottom: 20px; color: #666; font-size: 14px;">
          💡 슬라이드를 클릭하거나 더블클릭해보세요! 콘솔과 알림으로 이벤트를 확인할 수 있습니다.
        </p>
        <ScSwiper 
          v-bind="args" 
          @slide-click="handleSlideClick"
          @slide-double-click="handleSlideDoubleClick"
        >
          <template #slide="{ item, index }">
            <div class="example-slide" :style="{ background: item.background, cursor: 'pointer' }">
              <div class="slide-content">
                <h3>{{ item.title }}</h3>
                <p>{{ item.subtitle }}</p>
                <span class="slide-number">{{ index + 1 }}</span>
                <div style="margin-top: 10px; font-size: 12px; opacity: 0.8;">
                  클릭 또는 더블클릭
                </div>
              </div>
            </div>
          </template>
        </ScSwiper>
      </div>
    `,
  }),
};


\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
playground
\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
<route lang="yaml">
meta:
  title: Swiper
  description: SHC UI Swiper 컴포넌트입니다.
  author: 이강
  category: Swiper
</route>

<template>
  <div class="swiper-examples-page">
    <!-- Header -->
    <div class="page-header">
      <h1>ScSwiper 컴포넌트 예제</h1>
      <p>각 Effect별 개별 예제를 통한 ScSwiper 활용법</p>
    </div>

    <!-- Example 1: Slide Effect -->
    <div class="example-section">
      <h2 class="example-title">1. Slide Effect</h2>
      <p class="example-description">기본적인 좌우 슬라이딩 효과</p>
      <div class="swiper-container">
        <ScSwiper
          swiper-id="slide-example"
          :slides="slideExampleData"
          effect="slide"
          :slidesPerView="1"
          :spaceBetween="30"
          :centeredSlides="false"
          :pagination="true"
          paginationType="bullets"
          :navigation="true"
          :loop="false"
          size="medium"
          theme="default"
          :speed="300"
          @slide-click="onSlideClick"
          @slide-double-click="onSlideDoubleClick"
        >
          <template #slide="{ item, index }">
            <div
              class="example-slide clickable-slide"
              :style="{ background: item.background }"
            >
              <div class="slide-content">
                <h3>{{ item.title }}</h3>
                <p>{{ item.subtitle }}</p>
                <span class="slide-number">{{ index + 1 }}</span>
                <img
                  v-if="item.image"
                  :src="item.image"
                  :alt="item.title || `Slide ${index + 1}`"
                />
              </div>
            </div>
          </template>
        </ScSwiper>
      </div>
    </div>

    <!-- Example 2: Fade Effect -->
    <div class="example-section">
      <h2 class="example-title">2. Fade Effect</h2>
      <p class="example-description">부드러운 페이드 인/아웃 전환</p>
      <div class="swiper-container">
        <ScSwiper
          swiper-id="fade-example"
          :slides="fadeExampleData"
          effect="fade"
          :slidesPerView="1"
          :spaceBetween="0"
          :centeredSlides="false"
          :pagination="true"
          paginationType="fraction"
          :navigation="true"
          :loop="false"
          size="large"
          theme="dark"
          :speed="500"
          @slide-click="onSlideClick"
          @slide-double-click="onSlideDoubleClick"
        >
          <template #slide="{ item, index }">
            <div
              class="example-slide clickable-slide"
              :style="{ background: item.background }"
            >
              <div class="slide-content">
                <h3>{{ item.title }}</h3>
                <p>{{ item.subtitle }}</p>
                <span class="slide-number">{{ index + 1 }}</span>
              </div>
            </div>
          </template>
        </ScSwiper>
      </div>
    </div>

    <!-- Example 3: Cube Effect -->
    <div class="example-section">
      <h2 class="example-title">3. Cube Effect</h2>
      <p class="example-description">3D 큐브 회전 효과</p>
      <div class="swiper-container">
        <ScSwiper
          swiper-id="cube-example"
          :slides="cubeExampleData"
          effect="cube"
          :slidesPerView="1"
          :spaceBetween="0"
          :centeredSlides="false"
          :pagination="true"
          paginationType="bullets"
          :navigation="true"
          :loop="false"
          size="medium"
          theme="light"
          :speed="600"
        >
          <template #slide="{ item, index }">
            <div
              class="example-slide clickable-slide"
              :style="{ background: item.background }"
            >
              <div class="slide-content">
                <h3>{{ item.title }}</h3>
                <p>{{ item.subtitle }}</p>
                <span class="slide-number">{{ index + 1 }}</span>
              </div>
            </div>
          </template>
        </ScSwiper>
      </div>
    </div>

    <!-- Example 4: Coverflow Effect -->
    <div class="example-section">
      <h2 class="example-title">4. Coverflow Effect</h2>
      <p class="example-description">3D 커버플로우 스타일</p>
      <div
        class="swiper-container"
        style="height: 500px"
      >
        <ScSwiper
          swiper-id="coverflow-example"
          :slides="coverflowExampleData"
          effect="coverflow"
          slidesPerView="auto"
          :spaceBetween="50"
          :centeredSlides="true"
          :pagination="true"
          paginationType="progressbar"
          :navigation="true"
          :loop="false"
          size="large"
          theme="default"
          :speed="400"
        >
          <template #slide="{ item, index }">
            <div
              class="example-slide clickable-slide"
              :style="{ background: item.background }"
            >
              <div class="slide-content">
                <h3>{{ item.title }}</h3>
                <p>{{ item.subtitle }}</p>
                <span class="slide-number">{{ index + 1 }}</span>
              </div>
            </div>
          </template>
        </ScSwiper>
      </div>
    </div>

    <!-- Example 5: Flip Effect -->
    <div class="example-section">
      <h2 class="example-title">5. Flip Effect</h2>
      <p class="example-description">카드 뒤집기 효과</p>
      <div class="swiper-container">
        <ScSwiper
          swiper-id="flip-example"
          :slides="flipExampleData"
          effect="flip"
          :slidesPerView="1"
          :spaceBetween="0"
          :centeredSlides="false"
          :pagination="true"
          paginationType="bullets"
          :navigation="true"
          :loop="false"
          size="medium"
          theme="dark"
          :speed="600"
        >
          <template #slide="{ item, index }">
            <div
              class="example-slide clickable-slide"
              :style="{ background: item.background }"
            >
              <div class="slide-content">
                <h3>{{ item.title }}</h3>
                <p>{{ item.subtitle }}</p>
                <span class="slide-number">{{ index + 1 }}</span>
                <img
                  v-if="item.image"
                  :src="item.image"
                  :alt="item.title || `Slide ${index + 1}`"
                />
              </div>
            </div>
          </template>
        </ScSwiper>
      </div>
    </div>

    <!-- Example 6: Cards Effect -->
    <div class="example-section">
      <h2 class="example-title">6. Cards Effect</h2>
      <p class="example-description">카드 스택 효과</p>
      <div class="swiper-container">
        <ScSwiper
          swiper-id="cards-example"
          :slides="cardsExampleData"
          effect="cards"
          :slidesPerView="1"
          :spaceBetween="0"
          :centeredSlides="false"
          :pagination="true"
          paginationType="custom"
          :navigation="true"
          :loop="false"
          size="medium"
          theme="light"
          :speed="400"
        >
          <template #slide="{ item, index }">
            <div
              class="example-slide clickable-slide"
              :style="{ background: item.background }"
            >
              <div class="slide-content">
                <h3>{{ item.title }}</h3>
                <p>{{ item.subtitle }}</p>
                <span class="slide-number">{{ index + 1 }}</span>
              </div>
            </div>
          </template>
        </ScSwiper>
      </div>
    </div>

    <!-- Example 7: Creative Effect -->
    <div class="example-section">
      <h2 class="example-title">7. Creative Effect</h2>
      <p class="example-description">창의적인 3D 전환 효과</p>
      <div class="swiper-container">
        <ScSwiper
          swiper-id="creative-example"
          :slides="creativeExampleData"
          effect="creative"
          :slidesPerView="1"
          :spaceBetween="0"
          :centeredSlides="false"
          :pagination="true"
          paginationType="fraction"
          :navigation="true"
          :loop="false"
          size="large"
          theme="default"
          :speed="700"
        >
          <template #slide="{ item, index }">
            <div
              class="example-slide clickable-slide"
              :style="{ background: item.background }"
            >
              <div class="slide-content">
                <h3>{{ item.title }}</h3>
                <p>{{ item.subtitle }}</p>
                <span class="slide-number">{{ index + 1 }}</span>
                <img
                  v-if="item.image"
                  :src="item.image"
                  :alt="item.title || `Slide ${index + 1}`"
                />
              </div>
            </div>
          </template>
        </ScSwiper>
      </div>
    </div>

    <!-- Example 8: Cylinder Effect -->
    <div class="example-section">
      <h2 class="example-title">8. Cylinder Effect</h2>
      <p class="example-description">원통형 3D 회전 효과 (커스텀)</p>
      <div class="swiper-container">
        <ScSwiper
          swiper-id="cylinder-example"
          :slides="cylinderExampleData"
          effect="cylinder"
          :slidesPerView="3"
          :spaceBetween="0"
          :centeredSlides="true"
          :pagination="true"
          paginationType="bullets"
          :navigation="true"
          :loop="false"
          size="large"
          theme="dark"
          :speed="800"
          @slide-click="onSlideClick"
          @slide-double-click="onSlideDoubleClick"
        >
          <template #slide="{ item, index }">
            <div
              class="example-slide clickable-slide"
              :style="{ background: item.background }"
            >
              <div class="slide-content">
                <h3>{{ item.title }}</h3>
                <p>{{ item.subtitle }}</p>
                <span class="slide-number">{{ index + 1 }}</span>
                <img
                  v-if="item.image"
                  :src="item.image"
                  :alt="item.title || `Slide ${index + 1}`"
                />
              </div>
            </div>
          </template>
        </ScSwiper>
      </div>
    </div>

    <!-- Example 9: Vertical Direction -->
    <div class="example-section">
      <h2 class="example-title">9. Vertical Direction</h2>
      <p class="example-description">세로 방향 스와이프 (slide와 fade 효과만 지원)</p>
      <div class="swiper-container swiper-container--vertical">
        <ScSwiper
          swiper-id="vertical-example"
          :slides="verticalExampleData"
          effect="slide"
          direction="vertical"
          :slidesPerView="1"
          :spaceBetween="20"
          :centeredSlides="false"
          :pagination="true"
          paginationType="bullets"
          :navigation="true"
          :loop="false"
          size="large"
          theme="default"
          :speed="400"
          @slide-click="onSlideClick"
          @slide-double-click="onSlideDoubleClick"
        >
          <template #slide="{ item, index }">
            <div
              class="example-slide clickable-slide"
              :style="{ background: item.background }"
            >
              <div class="slide-content">
                <h3>{{ item.title }}</h3>
                <p>{{ item.subtitle }}</p>
                <span class="slide-number">{{ index + 1 }}</span>
                <img
                  v-if="item.image"
                  :src="item.image"
                  :alt="item.title || `Slide ${index + 1}`"
                />
              </div>
            </div>
          </template>
        </ScSwiper>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue";
import ScSwiper from "~/components/shc/swiper/ScSwiper.vue";

// ============================================================================
// EVENT HANDLERS
// ============================================================================
const onSlideClick = (event: any) => {
  console.log("슬라이드 클릭:", {
    index: event.index,
    title: event.slideData?.title,
    isActiveSlide: event.isActiveSlide,
    clickType: event.clickType,
  });

  // 실제 프로젝트에서는 라우팅, 모달 열기 등의 로직을 구현
  if (event.isActiveSlide) {
    console.log("현재 활성 슬라이드 클릭됨");
  }
};

const onSlideDoubleClick = (event: any) => {
  console.log("슬라이드 더블클릭:", {
    index: event.index,
    title: event.slideData?.title,
  });

  // 실제 프로젝트에서는 상세 페이지로 이동 등의 로직을 구현
  console.log("더블클릭으로 상세 페이지 이동");
};

// ============================================================================
// TYPES
// ============================================================================
interface SlideData {
  id: string;
  title: string;
  subtitle: string;
  background: string;
  image?: string;
}

// ============================================================================
// SLIDE DATA FOR EACH EFFECT
// ============================================================================

// 1. Slide Effect Data
const slideExampleData = ref<SlideData[]>([
  {
    id: "slide-1",
    title: "첫 번째 슬라이드",
    subtitle: "기본 슬라이딩 효과",
    background: "linear-gradient(45deg, #667eea, #764ba2)",
    image: "https://picsum.photos/200/100",
  },
  {
    id: "slide-2",
    title: "두 번째 슬라이드",
    subtitle: "좌우 이동 전환",
    background: "linear-gradient(45deg, #f093fb, #f5576c)",
    image: "https://picsum.photos/200/200",
  },
  {
    id: "slide-3",
    title: "세 번째 슬라이드",
    subtitle: "자연스러운 움직임",
    background: "linear-gradient(45deg, #4facfe, #00f2fe)",
    image: "https://picsum.photos/200/300",
  },
]);

// 2. Fade Effect Data
const fadeExampleData = ref<SlideData[]>([
  {
    id: "fade-1",
    title: "페이드 인",
    subtitle: "부드러운 나타남",
    background: "linear-gradient(135deg, #667eea 0%, #764ba2 100%)",
    image: "https://picsum.photos/200/300",
  },
  {
    id: "fade-2",
    title: "페이드 아웃",
    subtitle: "자연스러운 사라짐",
    background: "linear-gradient(135deg, #f093fb 0%, #f5576c 100%)",
    image: "https://picsum.photos/200/300",
  },
  {
    id: "fade-3",
    title: "페이드 전환",
    subtitle: "투명도 변화",
    background: "linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)",
    image: "https://picsum.photos/200/300",
  },
]);

// 3. Cube Effect Data
const cubeExampleData = ref<SlideData[]>([
  {
    id: "cube-1",
    title: "큐브 회전",
    subtitle: "3D 정육면체",
    background: "linear-gradient(45deg, #fa709a, #fee140)",
    image: "https://picsum.photos/200/100",
  },
  {
    id: "cube-2",
    title: "입체 전환",
    subtitle: "공간감 있는 이동",
    background: "linear-gradient(45deg, #a8edea, #fed6e3)",
    image: "https://picsum.photos/200/200",
  },
  {
    id: "cube-3",
    title: "회전 효과",
    subtitle: "역동적인 움직임",
    background: "linear-gradient(45deg, #ffecd2, #fcb69f)",
    image: "https://picsum.photos/200/400",
  },
]);

// 4. Coverflow Effect Data
const coverflowExampleData = ref<SlideData[]>([
  {
    id: "coverflow-1",
    title: "커버플로우 1",
    subtitle: "iTunes 스타일",
    background: "linear-gradient(45deg, #ff9a9e, #fecfef)",
  },
  {
    id: "coverflow-2",
    title: "커버플로우 2",
    subtitle: "앨범 커버 회전",
    background: "linear-gradient(45deg, #a8edea, #fed6e3)",
  },
  {
    id: "coverflow-3",
    title: "커버플로우 3",
    subtitle: "3D 회전 뷰",
    background: "linear-gradient(45deg, #fbc2eb, #a6c1ee)",
  },
  {
    id: "coverflow-4",
    title: "커버플로우 4",
    subtitle: "원근감 효과",
    background: "linear-gradient(45deg, #fa709a, #fee140)",
  },
  {
    id: "coverflow-5",
    title: "커버플로우 5",
    subtitle: "깊이감 표현",
    background: "linear-gradient(45deg, #667eea, #764ba2)",
  },
]);

// 5. Flip Effect Data
const flipExampleData = ref<SlideData[]>([
  {
    id: "flip-1",
    title: "카드 앞면",
    subtitle: "X축 회전",
    background: "linear-gradient(45deg, #667eea, #764ba2)",
  },
  {
    id: "flip-2",
    title: "카드 뒷면",
    subtitle: "뒤집기 효과",
    background: "linear-gradient(45deg, #f093fb, #f5576c)",
  },
  {
    id: "flip-3",
    title: "카드 정보",
    subtitle: "플립 애니메이션",
    background: "linear-gradient(45deg, #4facfe, #00f2fe)",
  },
]);

// 6. Cards Effect Data
const cardsExampleData = ref<SlideData[]>([
  {
    id: "cards-1",
    title: "골드 카드",
    subtitle: "스택 효과",
    background: "linear-gradient(45deg, #FFD700, #FFA500)",
  },
  {
    id: "cards-2",
    title: "실버 카드",
    subtitle: "카드 더미",
    background: "linear-gradient(45deg, #C0C0C0, #808080)",
  },
  {
    id: "cards-3",
    title: "플래티넘 카드",
    subtitle: "쌓인 형태",
    background: "linear-gradient(45deg, #E5E4E2, #BCC6CC)",
  },
]);

// 7. Creative Effect Data
const creativeExampleData = ref<SlideData[]>([
  {
    id: "creative-1",
    title: "창의적 전환 1",
    subtitle: "커스텀 3D 효과",
    background: "linear-gradient(45deg, #667eea, #764ba2)",
  },
  {
    id: "creative-2",
    title: "창의적 전환 2",
    subtitle: "독특한 애니메이션",
    background: "linear-gradient(45deg, #f093fb, #f5576c)",
  },
  {
    id: "creative-3",
    title: "창의적 전환 3",
    subtitle: "창의적 움직임",
    background: "linear-gradient(45deg, #4facfe, #00f2fe)",
  },
]);

// 8. Cylinder Effect Data
const cylinderExampleData = ref<SlideData[]>([
  {
    id: "cylinder-1",
    title: "실린더 1",
    subtitle: "원통형 회전",
    background: "linear-gradient(45deg, #ff9a9e, #fecfef)",
  },
  {
    id: "cylinder-2",
    title: "실린더 2",
    subtitle: "360도 회전",
    background: "linear-gradient(45deg, #a8edea, #fed6e3)",
  },
  {
    id: "cylinder-3",
    title: "실린더 3",
    subtitle: "입체 원통",
    background: "linear-gradient(45deg, #fbc2eb, #a6c1ee)",
  },
  {
    id: "cylinder-4",
    title: "실린더 4",
    subtitle: "3D 회전체",
    background: "linear-gradient(45deg, #667eea, #764ba2)",
  },
]);

// 9. Vertical Direction Data
const verticalExampleData = ref<SlideData[]>([
  {
    id: "vertical-1",
    title: "세로 스와이프 1",
    subtitle: "위아래 이동",
    background: "linear-gradient(180deg, #667eea, #764ba2)",
    image: "https://picsum.photos/200/300",
  },
  {
    id: "vertical-2",
    title: "세로 스와이프 2",
    subtitle: "수직 방향 전환",
    background: "linear-gradient(180deg, #f093fb, #f5576c)",
    image: "https://picsum.photos/200/300",
  },
  {
    id: "vertical-3",
    title: "세로 스와이프 3",
    subtitle: "세로 슬라이딩",
    background: "linear-gradient(180deg, #4facfe, #00f2fe)",
    image: "https://picsum.photos/200/300",
  },
  {
    id: "vertical-4",
    title: "세로 스와이프 4",
    subtitle: "수직 스크롤",
    background: "linear-gradient(180deg, #fa709a, #fee140)",
    image: "https://picsum.photos/200/300",
  },
]);
</script>

<style scoped>
.swiper-examples-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%);
  padding: 40px 20px;
  font-family: "Segoe UI", Tahoma, Geneva, Verdana, sans-serif;
}

/* Header */
.page-header {
  text-align: center;
  margin-bottom: 60px;
}

.page-header h1 {
  font-size: 2.5em;
  margin-bottom: 10px;
  color: #1f2937;
  font-weight: 700;
}

.page-header p {
  font-size: 1.1em;
  color: #6b7280;
  margin-bottom: 0;
}

/* Example Sections */
.example-section {
  max-width: 1200px;
  margin: 0 auto 60px;
  background: white;
  border-radius: 20px;
  padding: 30px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
}

.example-title {
  font-size: 1.8em;
  font-weight: 600;
  color: #1f2937;
  margin-bottom: 10px;
}

.example-description {
  font-size: 1em;
  color: #6b7280;
  margin-bottom: 30px;
  line-height: 1.6;
}

/* Swiper Container - 페이지별 레이아웃만 */
.swiper-container {
  height: 350px;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
  position: relative;
}

/* Vertical Direction Container */
.swiper-container--vertical {
  height: 450px; /* navigation 버튼 공간을 고려한 높이 조정 */
}

/* 클릭 가능한 슬라이드 스타일 */
.clickable-slide {
  cursor: pointer;
  transition:
    transform 0.2s ease,
    box-shadow 0.2s ease;
}

.clickable-slide:hover {
  transform: scale(1.02);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
}

.clickable-slide:active {
  transform: scale(0.98);
}

/* 나머지 슬라이드 스타일은 ScSwiper 컴포넌트 내장 스타일 사용 */

/* Responsive */
@media (max-width: 768px) {
  .swiper-examples-page {
    padding: 20px 10px;
  }

  .example-section {
    margin-bottom: 40px;
    padding: 20px;
  }

  .swiper-container {
    height: 280px;
  }

  .swiper-container--vertical {
    height: 400px; /* 모바일에서 navigation 버튼 공간을 고려한 높이 조정 */
  }
}
</style>



