<!-- SCSwiper with Web Accessibility -->
<!-- components/SCSwiper.vue - 웹접근성 추가 버전 -->
<template>
  <div
    :class="[containerClasses, `sc-swiper-${swiperId}`]"
    :data-effect="props.effect"
    role="region"
    :aria-describedby="`${accessibilityLabels.liveRegionId} ${accessibilityLabels.statusId}`"
    tabindex="0"
    @keydown="onContainerKeydown"
    @focus="onContainerFocus"
    @blur="onContainerBlur"
  >
    <!-- Skip Link -->
    <a 
      v-if="props.skipLinks"
      :href="`#${skipLinkId}`" 
      class="sr-only sr-only-focusable skip-link"
      @click="skipToEndOfCarousel"
    >
      {{ accessibilityLabels.skipToContent }}
    </a>

    <!-- Live Region for Screen Reader Announcements -->
    <div
      :id="accessibilityLabels.liveRegionId"
      aria-live="polite"
      aria-atomic="true"
      class="sr-only"
    >
      {{ liveRegionText }}
    </div>

    <!-- Status Region for Current State -->
    <div
      :id="accessibilityLabels.statusId"
      aria-live="polite"
      aria-atomic="false"
      class="sr-only"
    >
      현재 {{ currentSlideIndex + 1 }}번째 슬라이드, 총 {{ totalSlides }}개
    </div>

    <!-- Additional Announcements for Complex Interactions -->
    <div
      :id="accessibilityLabels.announcementsId"
      aria-live="assertive"
      aria-atomic="true"
      class="sr-only"
    >
      {{ announcements.join('. ') }}
    </div>

    <!-- Autoplay Control -->
    <button
      v-if="props.autoplay"
      :aria-label="isAutoplayPaused ? accessibilityLabels.playAutoplay : accessibilityLabels.pauseAutoplay"
      class="autoplay-control"
      @click="toggleAutoplay"
      @keydown="onAutoplayKeydown"
      type="button"
    >
      <span v-if="isAutoplayPaused" aria-hidden="true">▶</span>
      <span v-else aria-hidden="true">⏸</span>
    </button>

    <!-- Swiper 컨테이너 -->
    <swiper
      ref="swiperRef"
      :modules="modules"
      :pagination="paginationConfig"
      :navigation="navigationConfig"
      :scrollbar="scrollbarConfig"
      :autoplay="autoplayConfig"
      :a11y="a11yConfig"
      :loop="safeLoopMode"
      :slidesPerView="adjustedSlidesPerView"
      :spaceBetween="adjustedSpaceBetween"
      :centeredSlides="adjustedCenteredSlides"
      :direction="props.direction"
      :speed="props.speed"
      :effect="adjustedEffect"
      :breakpoints="props.breakpoints"
      :keyboard="keyboardConfig"
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
          @focus="onSlideFocus(index)"
          @keydown="onSlideKeydown($event, index)"
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
                role="img"
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
      @keydown="onNavigationKeydown($event, 'prev')"
      @focus="onNavigationFocus"
      @blur="onNavigationBlur"
    ></div>
    <div
      v-if="shouldShowNavigation"
      :class="props.direction === 'vertical' ? 'swiper-button-next-vertical' : 'swiper-button-next'"
      @keydown="onNavigationKeydown($event, 'next')"
      @focus="onNavigationFocus"
      @blur="onNavigationBlur"
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

    <!-- Accessibility Instructions (Hidden) -->
    <div class="sr-only" :id="`${swiperId}-instructions`">
      {{ accessibilityLabels.instructions }}
    </div>

    <!-- Skip Link Target -->
    <div 
      v-if="props.skipLinks"
      :id="skipLinkId" 
      class="sr-only" 
      tabindex="-1"
    >
      캐러셀 끝
    </div>
  </div>
</template>

<script setup lang="ts">
import {
  A11y,
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
  Keyboard,
} from "swiper/modules";
import { Swiper, SwiperSlide } from "swiper/vue";
import { computed, markRaw, onMounted, onUnmounted, reactive, shallowRef, ref, watch } from "vue";

// CSS imports
import "swiper/css";
import "swiper/css/a11y";
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
  // Accessibility props
  ariaLabel?: string;
  slideAriaLabelPrefix?: string;
  announceSlideChanges?: boolean;
  // 추가 웹접근성 props
  reduceMotion?: boolean;
  highContrast?: boolean;
  focusTrap?: boolean;
  announceAutoplay?: boolean;
  customInstructions?: string;
  skipLinks?: boolean;
  // A11y 모듈 설정
  a11y?: boolean | object;
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
  keyboard: Keyboard,
  a11y: A11y,
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
  announceSlideChanges: true,
  // 추가 웹접근성 기본값
  reduceMotion: false,
  highContrast: false,
  focusTrap: false,
  announceAutoplay: true,
  skipLinks: false,
  a11y: true,
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
const currentSlideIndex = ref(0);
const totalSlides = ref(0);
const liveRegionText = ref("");
const isAutoplayPaused = ref(false);
const focusedSlideIndex = ref(-1);
const announcements = ref<string[]>([]);
const isContainerFocused = ref(false);

// Computed
const swiperId = computed(() => props.swiperId || generateId());
const skipLinkId = ref(`skip-${swiperId.value}`);

const containerClasses = computed(() => {
  const baseClass = "sc-swiper-container";
  const sizeClass = `sc-swiper--${props.size}`;
  const themeClass = `sc-swiper--${props.theme}`;
  const directionClass = props.direction === "vertical" ? "sc-swiper--vertical" : "";
  const accessibilityClasses = [];
  
  if (props.reduceMotion) accessibilityClasses.push("sc-swiper--reduce-motion");
  if (props.highContrast) accessibilityClasses.push("sc-swiper--high-contrast");
  if (props.focusTrap) accessibilityClasses.push("sc-swiper--focus-trap");
  
  return [baseClass, sizeClass, themeClass, directionClass, ...accessibilityClasses];
});

const shouldShowNavigation = computed(() => props.navigation !== false);
const shouldShowPagination = computed(() => props.pagination !== false);
const shouldShowScrollbar = computed(() => props.scrollbar !== false);

// 커스텀 접근성 라벨 (A11y 모듈과 중복되지 않는 것들만)
const accessibilityLabels = computed(() => ({
  liveRegionId: `${swiperId.value}-live-region`,
  instructions: props.customInstructions || getDefaultInstructions(),
  playAutoplay: "자동 슬라이드쇼 재생",
  pauseAutoplay: "자동 슬라이드쇼 일시정지",
  skipToContent: `${swiperId.value} 캐러셀 건너뛰기`,
  announcementsId: `${swiperId.value}-announcements`,
  statusId: `${swiperId.value}-status`,
}));

// 기본 사용 설명서 생성
const getDefaultInstructions = () => {
  const baseInstructions = [];
  
  if (props.direction === "vertical") {
    baseInstructions.push("Use up and down arrow keys to navigate slides.");
  } else {
    baseInstructions.push("Use left and right arrow keys to navigate slides.");
  }
  
  baseInstructions.push("Use space or enter to activate buttons.");
  baseInstructions.push("Use Home key to go to first slide, End key to go to last slide.");
  
  if (props.autoplay) {
    baseInstructions.push("Press escape to pause autoplay.");
  }
  
  return baseInstructions.join(" ");
};

// 키보드 설정 - 커스텀 제어만 사용
const keyboardConfig = computed(() => ({
  enabled: false,
  onlyInViewport: false,
  pageUpDown: false,
}));

// 필요한 모듈들을 동적으로 계산
const modules = computed(() => {
  const moduleList = [];

  if (shouldShowPagination.value) moduleList.push(MODULE_MAP.pagination);
  if (shouldShowNavigation.value) moduleList.push(MODULE_MAP.navigation);
  if (shouldShowScrollbar.value) moduleList.push(MODULE_MAP.scrollbar);
  if (props.autoplay) moduleList.push(MODULE_MAP.autoplay);
  
  // Keyboard 모듈 제거 - 커스텀 키보드 핸들러 사용
  
  // A11y 모듈 추가
  if (props.a11y) moduleList.push(MODULE_MAP.a11y);

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
    renderBullet: (index: number, className: string) => {
      return `<button class="${className}" role="tab" aria-label="Go to slide ${index + 1}" aria-selected="false" tabindex="-1"></button>`;
    },
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
    pauseOnMouseEnter: true,
  };

  return typeof props.autoplay === "object" ? { ...config, ...props.autoplay } : config;
});

// A11y 설정
const a11yConfig = computed(() => {
  if (!props.a11y) return false;

  const config = {
    enabled: true,
    prevSlideMessage: props.direction === "vertical" ? "이전 슬라이드, 위로 이동" : "이전 슬라이드, 왼쪽으로 이동",
    nextSlideMessage: props.direction === "vertical" ? "다음 슬라이드, 아래로 이동" : "다음 슬라이드, 오른쪽으로 이동",
    firstSlideMessage: "첫 번째 슬라이드입니다",
    lastSlideMessage: "마지막 슬라이드입니다",
    paginationBulletMessage: "{{index}}번째 슬라이드로 이동",
    slideLabelMessage: "{{index}} / {{slidesLength}}",
    containerMessage: props.ariaLabel || `${totalSlides.value}개 슬라이드가 있는 이미지 캐러셀`,
    containerRoleDescriptionMessage: "캐러셀",
    itemRoleDescriptionMessage: "슬라이드",
    slideRole: "group",
    id: swiperId.value, // 각 인스턴스별 고유 ID
  };

  return typeof props.a11y === "object" ? { ...config, ...props.a11y } : config;
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

// Loop 모드 안전성 체크
const safeLoopMode = computed(() => {
  if (!props.loop) return false;
  
  const slideCount = props.slides?.length || 0;
  const slidesPerViewCount = typeof adjustedSlidesPerView.value === 'number' 
    ? adjustedSlidesPerView.value 
    : 1;
  
  // Loop 모드가 제대로 작동하려면 슬라이드 수가 slidesPerView의 최소 2배는 되어야 함
  const minSlidesForLoop = Math.max(slidesPerViewCount * 2, 3);
  
  if (slideCount < minSlidesForLoop) {
    console.warn(
      `Loop mode disabled: Need at least ${minSlidesForLoop} slides for slidesPerView=${slidesPerViewCount}, but only ${slideCount} slides provided.`
    );
    return false;
  }
  
  return true;
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
// ACCESSIBILITY METHODS
// ============================================================================

const updateLiveRegion = (message: string) => {
  if (props.announceSlideChanges) {
    liveRegionText.value = message;
    // Clear after announcement to avoid repetition
    setTimeout(() => {
      liveRegionText.value = "";
    }, 1000);
  }
};

// 추가적인 공지사항 관리
const addAnnouncement = (message: string) => {
  announcements.value.push(message);
  // 3초 후 공지사항 제거
  setTimeout(() => {
    announcements.value = announcements.value.filter(a => a !== message);
  }, 3000);
};

// 모션 감소 체크
const shouldReduceMotion = () => {
  if (props.reduceMotion) return true;
  if (typeof window !== 'undefined') {
    return window.matchMedia('(prefers-reduced-motion: reduce)').matches;
  }
  return false;
};

// 스킵 링크 핸들러
const skipToEndOfCarousel = (event: Event) => {
  event.preventDefault();
  const target = document.getElementById(skipLinkId.value);
  if (target) {
    target.focus();
    addAnnouncement("캐러셀을 건너뛰었습니다");
  }
};

// 포커스 트랩 관리
const manageFocusTrap = () => {
  if (!props.focusTrap) return;
  
  const container = document.querySelector(`.sc-swiper-${swiperId.value}`);
  if (!container) return;
  
  const focusableElements = container.querySelectorAll(
    'button, [href], input, select, textarea, [tabindex]:not([tabindex="-1"])'
  );
  
  if (focusableElements.length === 0) return;
  
  const firstElement = focusableElements[0] as HTMLElement;
  const lastElement = focusableElements[focusableElements.length - 1] as HTMLElement;
  
  const handleKeyDown = (e: Event) => {
    const keyboardEvent = e as KeyboardEvent;
    if (keyboardEvent.key === 'Tab') {
      if (keyboardEvent.shiftKey) {
        if (document.activeElement === firstElement) {
          keyboardEvent.preventDefault();
          lastElement.focus();
        }
      } else {
        if (document.activeElement === lastElement) {
          keyboardEvent.preventDefault();
          firstElement.focus();
        }
      }
    }
  };
  
  container.addEventListener('keydown', handleKeyDown);
  
  return () => {
    container.removeEventListener('keydown', handleKeyDown);
  };
};

// A11y 모듈에서 자동으로 처리되므로 별도 업데이트 불필요
const updatePaginationAccessibility = () => {
  // A11y 모듈에서 자동으로 처리됨
};

const toggleAutoplay = () => {
  const swiper = swiperRef.value?.swiper;
  if (!swiper) return;

  if (isAutoplayPaused.value) {
    swiper.autoplay.start();
    isAutoplayPaused.value = false;
    const message = "자동 슬라이드쇼가 재개되었습니다";
    updateLiveRegion(message);
    if (props.announceAutoplay) {
      addAnnouncement(message);
    }
  } else {
    swiper.autoplay.stop();
    isAutoplayPaused.value = true;
    const message = "자동 슬라이드쇼가 일시정지되었습니다";
    updateLiveRegion(message);
    if (props.announceAutoplay) {
      addAnnouncement(message);
    }
  }
};

// ============================================================================
// EVENT HANDLERS
// ============================================================================
const onSwiperInit = (swiper: any) => {
  totalSlides.value = swiper.slides.length;
  currentSlideIndex.value = swiper.activeIndex;
  
  // swiperRef에 실제 swiper 인스턴스 설정
  if (swiperRef.value) {
    swiperRef.value.swiper = swiper;
  }
  
  // Update pagination accessibility
  setTimeout(() => {
    updatePaginationAccessibility();
  }, 100);

  emit("init", swiper);

  // 컨테이너 포커스 가능하게 설정
  const container = document.querySelector(`.sc-swiper-${swiperId.value}`);
  if (container && !container.getAttribute('tabindex')) {
    container.setAttribute('tabindex', '0');
  }

  // Vertical direction일 때 navigation 버튼 위치 강제 조정
  if (props.direction === "vertical") {
    setTimeout(adjustVerticalNavigationButtons, 100);
  }
};

const onSlideChange = (swiper: any) => {
  currentSlideIndex.value = swiper.activeIndex;
  
  // Update pagination accessibility
  updatePaginationAccessibility();
  
  // Announce slide change
  const currentSlide = props.slides?.[swiper.activeIndex];
  const slideNumber = swiper.activeIndex + 1;
  const message = currentSlide?.title 
    ? `Slide ${slideNumber}: ${currentSlide.title}`
    : `Slide ${slideNumber} of ${totalSlides.value}`;
  
  updateLiveRegion(message);

  emit("slideChange", { activeIndex: swiper.activeIndex });
};

// 키보드 이벤트 핸들러 - 현재 포커스된 swiper만 제어
const onSwiperKeydown = (swiper: any, event: KeyboardEvent) => {
  // 현재 swiper가 포커스되어 있는지 확인
  const swiperContainer = document.querySelector(`.sc-swiper-${swiperId.value}`);
  const activeElement = document.activeElement;
  
  // 더 정확한 포커스 체크: 현재 swiper 컨테이너 내부에 포커스가 있는지 확인
  if (!swiperContainer?.contains(activeElement)) {
    return; // 현재 swiper에 포커스가 없으면 이벤트 무시
  }

  // 다른 swiper가 이미 이벤트를 처리했는지 확인 (이벤트 버블링 방지)
  if (event.defaultPrevented) {
    return;
  }

  const { key } = event;
  let handled = false;

  if (key === 'ArrowLeft' || (key === 'ArrowUp' && props.direction === 'vertical')) {
    event.preventDefault();
    event.stopPropagation();
    swiper.slidePrev();
    updateLiveRegion("이전 슬라이드로 이동했습니다");
    handled = true;
  } else if (key === 'ArrowRight' || (key === 'ArrowDown' && props.direction === 'vertical')) {
    event.preventDefault();
    event.stopPropagation();
    swiper.slideNext();
    updateLiveRegion("다음 슬라이드로 이동했습니다");
    handled = true;
  } else if (key === 'Home') {
    event.preventDefault();
    event.stopPropagation();
    swiper.slideTo(0);
    updateLiveRegion("첫 번째 슬라이드로 이동했습니다");
    addAnnouncement("첫 번째 슬라이드");
    handled = true;
  } else if (key === 'End') {
    event.preventDefault();
    event.stopPropagation();
    swiper.slideTo(totalSlides.value - 1);
    updateLiveRegion("마지막 슬라이드로 이동했습니다");
    addAnnouncement("마지막 슬라이드");
    handled = true;
  } else if (key === 'Escape' && props.autoplay && !isAutoplayPaused.value) {
    event.preventDefault();
    event.stopPropagation();
    toggleAutoplay();
    handled = true;
  } else if (key === 'PageUp') {
    event.preventDefault();
    event.stopPropagation();
    swiper.slidePrev();
    handled = true;
  } else if (key === 'PageDown') {
    event.preventDefault();
    event.stopPropagation();
    swiper.slideNext();
    handled = true;
  }

  // 키보드 이벤트 처리 완료
};

// 컨테이너 키보드 이벤트 핸들러 (우선순위 높음)
const onContainerKeydown = (event: KeyboardEvent) => {
  // 이 컨테이너가 포커스되어 있지 않으면 이벤트 무시
  if (!isContainerFocused.value) {
    return;
  }

  const swiper = swiperRef.value?.swiper;
  if (!swiper) {
    // 대체 방법: DOM에서 직접 swiper 인스턴스 찾기
    const swiperElement = document.querySelector(`.sc-swiper-${swiperId.value} .swiper`);
    if (swiperElement && (swiperElement as any).swiper) {
      const directSwiper = (swiperElement as any).swiper;
      handleKeyboardEvent(directSwiper, event);
      return;
    }
    return;
  }

  handleKeyboardEvent(swiper, event);
};

// 키보드 이벤트 처리 함수 (공통 로직)
const handleKeyboardEvent = (swiperInstance: any, event: KeyboardEvent) => {
  const { key } = event;

  if (key === 'ArrowLeft' || (key === 'ArrowUp' && props.direction === 'vertical')) {
    event.preventDefault();
    event.stopPropagation();
    swiperInstance.slidePrev();
    updateLiveRegion("이전 슬라이드로 이동했습니다");
  } else if (key === 'ArrowRight' || (key === 'ArrowDown' && props.direction === 'vertical')) {
    event.preventDefault();
    event.stopPropagation();
    swiperInstance.slideNext();
    updateLiveRegion("다음 슬라이드로 이동했습니다");
  } else if (key === 'Home') {
    event.preventDefault();
    event.stopPropagation();
    swiperInstance.slideTo(0);
    updateLiveRegion("첫 번째 슬라이드로 이동했습니다");
    addAnnouncement("첫 번째 슬라이드");
  } else if (key === 'End') {
    event.preventDefault();
    event.stopPropagation();
    swiperInstance.slideTo(totalSlides.value - 1);
    updateLiveRegion("마지막 슬라이드로 이동했습니다");
    addAnnouncement("마지막 슬라이드");
  } else if (key === 'Escape' && props.autoplay && !isAutoplayPaused.value) {
    event.preventDefault();
    event.stopPropagation();
    toggleAutoplay();
  } else if (key === 'PageUp') {
    event.preventDefault();
    event.stopPropagation();
    swiperInstance.slidePrev();
  } else if (key === 'PageDown') {
    event.preventDefault();
    event.stopPropagation();
    swiperInstance.slideNext();
  }
};

// 컨테이너 포커스 핸들러
const onContainerFocus = (event: FocusEvent) => {
  isContainerFocused.value = true;
  
  // 다른 컨테이너들의 포커스 해제
  const allContainers = document.querySelectorAll('[class*="sc-swiper-"]');
  allContainers.forEach(container => {
    if (container !== event.currentTarget) {
      (container as HTMLElement).style.outline = '';
    }
  });
  
  // 현재 컨테이너 포커스 스타일 적용
  const currentContainer = event.currentTarget as HTMLElement;
  currentContainer.style.outline = '3px solid #007aff';
  currentContainer.style.outlineOffset = '2px';
};

// 컨테이너 블러 핸들러
const onContainerBlur = (event: FocusEvent) => {
  const container = event.currentTarget as HTMLElement;
  const relatedTarget = event.relatedTarget as HTMLElement;
  
  // 포커스가 컨테이너 외부로 이동한 경우에만 블러 처리
  if (!relatedTarget || !container.contains(relatedTarget)) {
    isContainerFocused.value = false;
    container.style.outline = '';
    container.style.outlineOffset = '';
  }
};

const onSlideKeydown = (event: KeyboardEvent, index: number) => {
  const { key } = event;
  const swiper = swiperRef.value?.swiper;
  if (!swiper) return;

  switch (key) {
    case 'Enter':
    case ' ':
      event.preventDefault();
      event.stopPropagation();
      if (index !== currentSlideIndex.value) {
        swiper.slideTo(index);
      }
      break;
    case 'ArrowLeft':
    case 'ArrowUp':
      event.preventDefault();
      event.stopPropagation();
      swiper.slidePrev();
      break;
    case 'ArrowRight':
    case 'ArrowDown':
      event.preventDefault();
      event.stopPropagation();
      swiper.slideNext();
      break;
  }
};

const onSlideFocus = (index: number) => {
  const swiper = swiperRef.value?.swiper;
  if (swiper && index !== currentSlideIndex.value) {
    swiper.slideTo(index);
  }
};

const onNavigationKeydown = (event: KeyboardEvent, direction: 'prev' | 'next') => {
  const { key } = event;
  if (key === 'Enter' || key === ' ') {
    event.preventDefault();
    event.stopPropagation();
    const swiper = swiperRef.value?.swiper;
    if (swiper) {
      if (direction === 'prev') {
        swiper.slidePrev();
      } else {
        swiper.slideNext();
      }
    }
  }
};

const onNavigationFocus = (event: FocusEvent) => {
  const target = event.target as HTMLElement;
  target.style.outline = '2px solid #007aff';
  target.style.outlineOffset = '2px';
};

const onNavigationBlur = (event: FocusEvent) => {
  const target = event.target as HTMLElement;
  target.style.outline = '';
  target.style.outlineOffset = '';
};

const onAutoplayKeydown = (event: KeyboardEvent) => {
  const { key } = event;
  if (key === 'Enter' || key === ' ') {
    event.preventDefault();
    toggleAutoplay();
  }
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
  // 컨테이너 초기 설정
  setTimeout(() => {
    const container = document.querySelector(`.sc-swiper-${swiperId.value}`);
    if (container) {
      if (!container.getAttribute('tabindex')) {
        container.setAttribute('tabindex', '0');
      }
      if (!container.getAttribute('aria-label')) {
        container.setAttribute('aria-label', `슬라이드 컨테이너 ${swiperId.value}`);
      }
    }
  }, 50);

  // Vertical direction 버튼 위치 조정
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

      const handleResize = () => {
        adjustVerticalNavigationButtons();
      };

      window.addEventListener("resize", handleResize);

      onUnmounted(() => {
        observer.disconnect();
        window.removeEventListener("resize", handleResize);
      });
    }

    setTimeout(adjustVerticalNavigationButtons, 100);
  }

  // 포커스 트랩 설정
  let cleanupFocusTrap: (() => void) | undefined;
  if (props.focusTrap) {
    cleanupFocusTrap = manageFocusTrap();
  }

  // 모션 감소 설정 적용
  if (shouldReduceMotion()) {
    const container = document.querySelector(`.sc-swiper-${swiperId.value}`);
    if (container) {
      container.classList.add('sc-swiper--reduce-motion');
    }
  }

  onUnmounted(() => {
    if (cleanupFocusTrap) {
      cleanupFocusTrap();
    }
  });
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
  toggleAutoplay,
});
</script>

<style scoped>
/* Screen Reader Only 클래스 */
.sr-only {
  position: absolute;
  width: 1px;
  height: 1px;
  padding: 0;
  margin: -1px;
  overflow: hidden;
  clip: rect(0, 0, 0, 0);
  white-space: nowrap;
  border: 0;
}

/* Screen Reader Only - 포커스 시 표시 */
.sr-only-focusable:focus {
  position: static;
  width: auto;
  height: auto;
  padding: 0.5rem 1rem;
  margin: 0;
  overflow: visible;
  clip: auto;
  white-space: normal;
  background: #000;
  color: #fff;
  text-decoration: none;
  z-index: 1000;
}

/* Skip Link 스타일 */
.skip-link {
  position: absolute;
  top: -40px;
  left: 6px;
  background: #000;
  color: #fff;
  padding: 8px;
  border-radius: 4px;
  text-decoration: none;
  z-index: 1000;
  transition: top 0.3s;
}

.skip-link:focus {
  top: 6px;
}

/* Autoplay Control 버튼 */
.autoplay-control {
  position: absolute;
  top: 10px;
  right: 10px;
  z-index: 20;
  background: rgba(0, 0, 0, 0.7);
  color: white;
  border: none;
  border-radius: 50%;
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  font-size: 16px;
  transition: all 0.3s ease;
}

.autoplay-control:hover {
  background: rgba(0, 0, 0, 0.9);
  transform: scale(1.1);
}

.autoplay-control:focus {
  outline: 2px solid #007aff;
  outline-offset: 2px;
}

/* ============================================================================
   기본 스타일 (기존 스타일 유지)
   ============================================================================ */
.sc-swiper-container {
  position: relative;
  width: 100%;
  outline: none; /* 기본 outline 제거 */
}

/* 포커스된 컨테이너 스타일 */
.sc-swiper-container:focus {
  outline: 3px solid #007aff;
  outline-offset: 2px;
  border-radius: 4px;
}

/* 포커스 시 추가 시각적 피드백 */
.sc-swiper-container:focus-visible {
  outline: 3px solid #007aff;
  outline-offset: 2px;
  border-radius: 4px;
  box-shadow: 0 0 0 6px rgba(0, 122, 255, 0.2);
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
   Navigation 스타일 - 포커스 개선
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

:deep(.swiper-button-next:focus),
:deep(.swiper-button-prev:focus) {
  outline: 2px solid #007aff !important;
  outline-offset: 2px !important;
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
   Pagination 스타일 - 접근성 개선
   ============================================================================ */
:deep(.swiper-pagination) {
  z-index: 10 !important;
  position: relative !important;
}

:deep(.swiper-pagination-bullet) {
  background: rgba(0, 0, 0, 0.3) !important;
  opacity: 1 !important;
  transition: all 0.3s ease !important;
  border: none !important;
  cursor: pointer !important;
}

:deep(.swiper-pagination-bullet:focus) {
  outline: 2px solid #007aff !important;
  outline-offset: 2px !important;
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
   웹접근성 스타일
   ============================================================================ */

/* 모션 감소 설정 */
.sc-swiper--reduce-motion :deep(.swiper-slide),
.sc-swiper--reduce-motion :deep(.swiper-wrapper),
.sc-swiper--reduce-motion :deep(.swiper-button-next),
.sc-swiper--reduce-motion :deep(.swiper-button-prev),
.sc-swiper--reduce-motion :deep(.swiper-pagination-bullet) {
  transition-duration: 0.01ms !important;
  animation-duration: 0.01ms !important;
  animation-iteration-count: 1 !important;
  scroll-behavior: auto !important;
}

/* 고대비 모드 */
.sc-swiper--high-contrast {
  filter: contrast(150%);
}

.sc-swiper--high-contrast :deep(.swiper-button-next),
.sc-swiper--high-contrast :deep(.swiper-button-prev) {
  background: #000 !important;
  color: #fff !important;
  border: 2px solid #fff !important;
}

.sc-swiper--high-contrast :deep(.swiper-pagination-bullet) {
  background: #000 !important;
  border: 1px solid #fff !important;
}

.sc-swiper--high-contrast :deep(.swiper-pagination-bullet-active) {
  background: #fff !important;
  border: 1px solid #000 !important;
}

/* 포커스 트랩 모드 */
.sc-swiper--focus-trap {
  outline: 2px solid transparent;
  outline-offset: 2px;
}

.sc-swiper--focus-trap:focus-within {
  outline-color: #007aff;
}

/* ============================================================================
   개선된 포커스 스타일
   ============================================================================ */

/* 모든 포커스 가능한 요소에 대한 고대비 포커스 표시 */
:deep(.swiper-button-next):focus,
:deep(.swiper-button-prev):focus,
:deep(.swiper-pagination-bullet):focus,
.autoplay-control:focus {
  outline: 3px solid #007aff !important;
  outline-offset: 2px !important;
  box-shadow: 0 0 0 6px rgba(0, 122, 255, 0.3) !important;
}

/* 슬라이드 포커스 스타일 */
:deep(.swiper-slide):focus {
  outline: 2px solid #007aff !important;
  outline-offset: 2px !important;
  z-index: 10 !important;
}

/* 키보드 사용자를 위한 추가 시각적 피드백 */
:deep(.swiper-button-next):focus:not(:hover),
:deep(.swiper-button-prev):focus:not(:hover) {
  transform: scale(1.1) !important;
}

/* Windows 고대비 모드 지원 */
@media (prefers-contrast: high) {
  .sc-swiper-container {
    outline: 1px solid;
  }
  
  :deep(.swiper-button-next),
  :deep(.swiper-button-prev) {
    background: ButtonFace !important;
    color: ButtonText !important;
    border: 1px solid ButtonText !important;
  }
  
  :deep(.swiper-pagination-bullet) {
    background: ButtonText !important;
    border: 1px solid ButtonFace !important;
  }
}

/* 시각 장애인을 위한 추가 스타일 */
@media (prefers-reduced-motion: reduce) {
  .sc-swiper-container :deep(*) {
    animation-duration: 0.01ms !important;
    animation-iteration-count: 1 !important;
    transition-duration: 0.01ms !important;
    scroll-behavior: auto !important;
  }
}

/* 터치 인터페이스에서 접근성 개선 */
@media (hover: none) and (pointer: coarse) {
  :deep(.swiper-button-next),
  :deep(.swiper-button-prev) {
    width: 60px !important;
    height: 60px !important;
  }
  
  :deep(.swiper-pagination-bullet) {
    width: 16px !important;
    height: 16px !important;
    margin: 0 8px !important;
  }
}

/* ============================================================================
   반응형
   ============================================================================ */
@media (max-width: 768px) {
  .autoplay-control {
    width: 32px;
    height: 32px;
    font-size: 14px;
    top: 5px;
    right: 5px;
  }

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
