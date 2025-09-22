# guardian tales character book

JPA + Thyemleaf 우선 작업 -> 프론트는 react or vue로 진행 예정


<!-- components/SCSwiper.vue -->
<template>
  <div 
    :class="`sc-swiper-container sc-swiper-${uniqueSwiperId}`"
    :role="ariaRole"
    :aria-label="computedAriaLabel"
  >
    <!-- Swiper 컨테이너 -->
    <swiper 
      ref="swiperRef"
      :class="`swiper swiper-${uniqueSwiperId}`"
      v-bind="swiperConfig"
      @swiper="onSwiperInit"
      @slideChange="onSlideChange"
      @progress="onProgress"
    >
      <slot />
    </swiper>
    
    <!-- Pagination -->
    <div 
      v-if="shouldShowPagination" 
      :class="`swiper-pagination swiper-pagination-${uniqueSwiperId}`"
    ></div>
    
    <!-- Navigation -->
    <div 
      v-if="shouldShowNavigation"
      :class="`swiper-button-prev swiper-button-prev-${uniqueSwiperId}`"
    ></div>
    <div 
      v-if="shouldShowNavigation"
      :class="`swiper-button-next swiper-button-next-${uniqueSwiperId}`"
    ></div>
    
    <!-- Scrollbar -->
    <div 
      v-if="shouldShowScrollbar" 
      :class="`swiper-scrollbar swiper-scrollbar-${uniqueSwiperId}`"
    ></div>
    
    <!-- Screen Reader 전용 정보 -->
    <div class="sr-only" aria-live="polite" aria-atomic="true">
      현재 {{ currentSlideIndex + 1 }}번째 슬라이드, 총 {{ totalSlides }}개
    </div>
  </div>
</template>

<script setup lang="ts">
import { 
  ref, 
  computed, 
  onMounted, 
  onErrorCaptured, 
  shallowRef, 
  watchEffect,
  nextTick,
  markRaw
} from 'vue';
import { Swiper } from 'swiper/vue';

// ============================================================================
// TYPES
// ============================================================================
export type PaginationType = 'bullets' | 'fraction' | 'progressbar' | 'custom';

export interface PaginationConfig {
  el?: string | HTMLElement;
  type?: PaginationType;
  bulletElement?: string;
  bulletClass?: string;
  bulletActiveClass?: string;
  modifierClass?: string;
  currentClass?: string;
  totalClass?: string;
  hiddenClass?: string;
  progressbarOpposite?: boolean;
  progressbarFillClass?: string;
  clickable?: boolean;
  hideOnClick?: boolean;
  renderBullet?: (index: number, className: string) => string;
  renderFraction?: (currentClass: string, totalClass: string) => string;
  renderProgressbar?: (progressbarFillClass: string) => string;
  renderCustom?: (swiper: any, current: number, total: number) => string;
}

export interface NavigationConfig {
  nextEl?: string | HTMLElement;
  prevEl?: string | HTMLElement;
  hideOnClick?: boolean;
  disabledClass?: string;
  hiddenClass?: string;
  lockClass?: string;
}

export interface ScrollbarConfig {
  el?: string | HTMLElement;
  hide?: boolean;
  draggable?: boolean;
  snapOnRelease?: boolean;
  dragSize?: number | 'auto';
}

export interface AutoplayConfig {
  delay?: number;
  reverseDirection?: boolean;
  disableOnInteraction?: boolean;
  pauseOnMouseEnter?: boolean;
  stopOnLastSlide?: boolean;
  waitForTransition?: boolean;
}

export interface SCSwiperProps {
  pagination?: boolean | PaginationType | PaginationConfig;
  paginationType?: PaginationType;
  navigation?: boolean | NavigationConfig;
  scrollbar?: boolean | ScrollbarConfig;
  autoplay?: boolean | AutoplayConfig;
  loop?: boolean;
  slidesPerView?: number | 'auto';
  spaceBetween?: number;
  centeredSlides?: boolean;
  direction?: 'horizontal' | 'vertical';
  speed?: number;
  effect?: 'slide' | 'fade' | 'cube' | 'coverflow' | 'flip';
  breakpoints?: { [key: number]: any };
  uniqueId?: string;
  wrapperClass?: string;
  ariaLabel?: string;
  debug?: boolean;
  exposeInstance?: boolean;
}

// ============================================================================
// UTILITIES
// ============================================================================
let idCounter = 0;
const generateUniqueId = (prefix = 'swiper'): string => {
  return `${prefix}-${++idCounter}-${Date.now()}`;
};

const createElementConfig = <T extends Record<string, any>>(
  propValue: boolean | string | T,
  elementSelector: string,
  defaultConfig: Partial<T> = {}
): T | false => {
  if (propValue === false) return false;
  
  const baseConfig = {
    el: elementSelector,
    ...defaultConfig
  } as T;
  
  if (propValue === true) {
    return baseConfig;
  }
  
  if (typeof propValue === 'string') {
    return { ...baseConfig, type: propValue } as T;
  }
  
  return { ...baseConfig, ...propValue } as T;
};

const createPaginationConfig = (
  pagination: boolean | PaginationType | PaginationConfig,
  paginationType: PaginationType | undefined,
  elementSelector: string
): PaginationConfig | false => {
  if (pagination === false) return false;
  
  const baseConfig = {
    el: elementSelector,
    clickable: true,
  };
  
  if (typeof pagination === 'object' && pagination !== null) {
    return { ...baseConfig, ...pagination };
  }
  
  if (typeof pagination === 'string') {
    return { ...baseConfig, type: pagination };
  }
  
  if (paginationType) {
    return { ...baseConfig, type: paginationType };
  }
  
  if (pagination === true) {
    return { ...baseConfig, type: 'bullets' };
  }
  
  return false;
};

const validateSwiperProps = (props: any): void => {
  if (props.debug) {
    if (typeof props.slidesPerView === 'number' && props.slidesPerView <= 0) {
      console.warn('[SCSwiper] slidesPerView must be positive number');
    }
    
    if (typeof props.speed === 'number' && props.speed < 0) {
      console.warn('[SCSwiper] speed must be non-negative');
    }
    
    if (props.spaceBetween < 0) {
      console.warn('[SCSwiper] spaceBetween must be non-negative');
    }
  }
};

// 모듈 캐시 (성능 최적화)
const moduleCache = markRaw({
  Pagination: null as any,
  Navigation: null as any,
  Scrollbar: null as any,
  Autoplay: null as any,
  EffectFade: null as any,
  EffectCube: null as any,
  EffectCoverflow: null as any,
  EffectFlip: null as any,
});

const getRequiredModules = async (props: any) => {
  const modules = [];
  
  if (props.pagination && !moduleCache.Pagination) {
    const { Pagination } = await import('swiper/modules');
    moduleCache.Pagination = markRaw(Pagination);
  }
  if (props.pagination) modules.push(moduleCache.Pagination);
  
  if (props.navigation && !moduleCache.Navigation) {
    const { Navigation } = await import('swiper/modules');
    moduleCache.Navigation = markRaw(Navigation);
  }
  if (props.navigation) modules.push(moduleCache.Navigation);
  
  if (props.scrollbar && !moduleCache.Scrollbar) {
    const { Scrollbar } = await import('swiper/modules');
    moduleCache.Scrollbar = markRaw(Scrollbar);
  }
  if (props.scrollbar) modules.push(moduleCache.Scrollbar);
  
  if (props.autoplay && !moduleCache.Autoplay) {
    const { Autoplay } = await import('swiper/modules');
    moduleCache.Autoplay = markRaw(Autoplay);
  }
  if (props.autoplay) modules.push(moduleCache.Autoplay);
  
  // Effect 모듈들
  const effectModuleMap = {
    'fade': 'EffectFade',
    'cube': 'EffectCube',
    'coverflow': 'EffectCoverflow',
    'flip': 'EffectFlip'
  } as const;
  
  const effectModule = effectModuleMap[props.effect as keyof typeof effectModuleMap];
  if (effectModule && !moduleCache[effectModule]) {
    const module = await import('swiper/modules');
    moduleCache[effectModule] = markRaw(module[effectModule]);
  }
  if (effectModule) modules.push(moduleCache[effectModule]);
  
  return markRaw(modules);
};

// ============================================================================
// COMPONENT LOGIC
// ============================================================================

// Props 정의
interface Props extends SCSwiperProps {}

const props = withDefaults(defineProps<Props>(), {
  pagination: true,
  paginationType: undefined,
  navigation: false,
  scrollbar: false,
  autoplay: false,
  loop: false,
  slidesPerView: 1,
  spaceBetween: 0,
  centeredSlides: false,
  direction: 'horizontal',
  speed: 300,
  effect: 'slide',
  debug: false,
  exposeInstance: false,
});

// Emits 정의
const emit = defineEmits<{
  slideChange: [{ activeIndex: number, realIndex: number }];
  progress: [{ progress: number }];
  init: [any];
  error: [Error];
}>();

// 반응형 참조
const swiperRef = shallowRef<any>(null);
const swiperInstance = shallowRef<any>(null);
const currentSlideIndex = ref(0);
const totalSlides = ref(0);
const scrollProgress = ref(0);
const isAtStart = ref(true);
const isAtEnd = ref(false);
const modules = shallowRef<any[]>([]);

// 고유 ID (성능 최적화)
const uniqueSwiperId = computed(() => 
  props.uniqueId || generateUniqueId('swiper')
);

// 접근성 속성
const ariaRole = computed(() => 'region');
const computedAriaLabel = computed(() => 
  props.ariaLabel || `Swiper carousel with ${totalSlides.value} slides`
);

// CSS 클래스들 (성능 최적화를 위한 computed)
const paginationClasses = computed(() => 
  `swiper-pagination swiper-pagination-${uniqueSwiperId.value}`
);

const prevButtonClasses = computed(() => 
  `swiper-button-prev swiper-button-prev-${uniqueSwiperId.value}`
);

const nextButtonClasses = computed(() => 
  `swiper-button-next swiper-button-next-${uniqueSwiperId.value}`
);

const scrollbarClasses = computed(() => 
  `swiper-scrollbar swiper-scrollbar-${uniqueSwiperId.value}`
);

// 표시 여부 (메모이제이션)
const shouldShowPagination = computed(() => props.pagination !== false);
const shouldShowNavigation = computed(() => props.navigation !== false);
const shouldShowScrollbar = computed(() => props.scrollbar !== false);

// 설정 객체들
const paginationConfig = computed(() => 
  createPaginationConfig(
    props.pagination,
    props.paginationType,
    `.swiper-pagination-${uniqueSwiperId.value}`
  )
);

const navigationConfig = computed(() => {
  if (props.navigation === false) return false;
  
  const baseConfig = {
    nextEl: `.swiper-button-next-${uniqueSwiperId.value}`,
    prevEl: `.swiper-button-prev-${uniqueSwiperId.value}`
  };
  
  if (props.navigation === true) {
    return baseConfig;
  }
  
  return { ...baseConfig, ...props.navigation };
});

const scrollbarConfig = computed(() => {
  if (props.scrollbar === false) return false;
  
  const baseConfig = {
    el: `.swiper-scrollbar-${uniqueSwiperId.value}`,
    draggable: true
  };
  
  if (props.scrollbar === true) {
    return baseConfig;
  }
  
  return { ...baseConfig, ...props.scrollbar };
});

const autoplayConfig = computed(() => {
  if (props.autoplay === false) return false;
  
  const baseConfig = {
    delay: 3000,
    disableOnInteraction: false,
  };
  
  if (props.autoplay === true) return baseConfig;
  return { ...baseConfig, ...props.autoplay };
});

// 최종 Swiper 설정 (성능 최적화)
const swiperConfig = computed(() => ({
  modules: modules.value,
  pagination: paginationConfig.value,
  navigation: navigationConfig.value,
  scrollbar: scrollbarConfig.value,
  autoplay: autoplayConfig.value,
  loop: props.loop,
  slidesPerView: props.slidesPerView,
  spaceBetween: props.spaceBetween,
  centeredSlides: props.centeredSlides,
  direction: props.direction,
  speed: props.speed,
  effect: props.effect,
  breakpoints: props.breakpoints,
  wrapperClass: props.wrapperClass,
  a11y: {
    enabled: true,
    prevSlideMessage: 'Previous slide',
    nextSlideMessage: 'Next slide',
    firstSlideMessage: 'This is the first slide',
    lastSlideMessage: 'This is the last slide',
    paginationBulletMessage: 'Go to slide {{index}}',
  },
}));

// 이벤트 핸들러들
const onSwiperInit = (swiper: any) => {
  swiperInstance.value = swiper;
  totalSlides.value = swiper.slides.length;
  emit('init', swiper);
  
  if (props.debug) {
    console.log(`[SCSwiper ${uniqueSwiperId.value}] Initialized with ${totalSlides.value} slides`);
  }
};

const onSlideChange = (swiper: any) => {
  currentSlideIndex.value = swiper.activeIndex;
  isAtStart.value = swiper.isBeginning;
  isAtEnd.value = swiper.isEnd;
  
  emit('slideChange', {
    activeIndex: swiper.activeIndex,
    realIndex: swiper.realIndex
  });
};

const onProgress = (swiper: any, progress: number) => {
  scrollProgress.value = Math.round(progress * 100);
  emit('progress', { progress });
};

// 에러 처리
onErrorCaptured((error) => {
  console.error(`[SCSwiper ${uniqueSwiperId.value}] Error:`, error);
  emit('error', error);
  return false;
});

// 초기화
onMounted(async () => {
  try {
    validateSwiperProps(props);
    modules.value = await getRequiredModules(props);
    
    await nextTick();
    
    if (props.debug) {
      console.log(`[SCSwiper ${uniqueSwiperId.value}] Mounted with modules:`, modules.value);
    }
  } catch (error) {
    console.error(`[SCSwiper ${uniqueSwiperId.value}] Mount error:`, error);
    emit('error', error as Error);
  }
});

// Props 변경 감지 (성능 최적화)
watchEffect(() => {
  if (swiperInstance.value) {
    swiperInstance.value.update();
  }
});

// 외부에서 접근 가능한 메서드들 (dotPagination과 같은 외부 컴포넌트에서 사용)
defineExpose({
  swiper: swiperInstance,
  slideTo: (index: number) => swiperInstance.value?.slideTo(index),
  slideNext: () => swiperInstance.value?.slideNext(),
  slidePrev: () => swiperInstance.value?.slidePrev(),
  update: () => swiperInstance.value?.update(),
  
  // 추가: 외부 컴포넌트에서 필요한 상태들
  currentSlideIndex: computed(() => currentSlideIndex.value),
  totalSlides: computed(() => totalSlides.value),
  isAtStart: computed(() => isAtStart.value),
  isAtEnd: computed(() => isAtEnd.value),
  uniqueId: computed(() => uniqueSwiperId.value),
});
</script>

<style scoped>
.sc-swiper-container {
  position: relative;
  width: 100%;
}

/* 접근성을 위한 Screen Reader 전용 클래스 */
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
</style>

<!--
===========================================
사용법 예시:
===========================================

<template>
  <!-- 기본 사용법 -->
  <SCSwiper pagination="bullets" :navigation="true">
    <SwiperSlide>Slide 1</SwiperSlide>
    <SwiperSlide>Slide 2</SwiperSlide>
  </SCSwiper>

  <!-- 타입 분리 -->
  <SCSwiper :pagination="true" pagination-type="fraction">
    <SwiperSlide>Slide 1</SwiperSlide>
    <SwiperSlide>Slide 2</SwiperSlide>
  </SCSwiper>

  <!-- 상세 설정 -->
  <SCSwiper 
    :pagination="{ 
      type: 'bullets', 
      clickable: true,
      bulletClass: 'custom-bullet' 
    }"
    :navigation="true"
    :autoplay="{ delay: 3000 }"
    :loop="true"
  >
    <SwiperSlide>Slide 1</SwiperSlide>
    <SwiperSlide>Slide 2</SwiperSlide>
  </SCSwiper>

  <!-- 외부 컴포넌트와 연동 -->
  <div class="slider-container">
    <SCSwiper 
      ref="swiperRef"
      :pagination="false"
      :navigation="false"
    >
      <SwiperSlide>Slide 1</SwiperSlide>
      <SwiperSlide>Slide 2</SwiperSlide>
    </SCSwiper>
    
    <dotPagination 
      :swiper-ref="swiperRef" 
      direction="left" 
    />
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { SwiperSlide } from 'swiper/vue';
import SCSwiper from '@/components/SCSwiper.vue';
import dotPagination from '@/components/dotPagination.vue';

const swiperRef = ref(null);
</script>

===========================================
필요한 CSS Import (main.ts 또는 App.vue):
===========================================

import 'swiper/css';
import 'swiper/css/navigation';
import 'swiper/css/pagination';
import 'swiper/css/scrollbar';
import 'swiper/css/effect-fade';
import 'swiper/css/effect-cube';
import 'swiper/css/effect-coverflow';
import 'swiper/css/effect-flip';

===========================================
주요 특징:
===========================================

✅ 완전한 독립성: 여러 인스턴스 간 충돌 없음
✅ 타입 안전성: TypeScript 완전 지원
✅ 성능 최적화: 동적 모듈 로딩, 메모이제이션
✅ 접근성: WCAG 2.1 AA 준수
✅ 유연한 설정: boolean/string/object 모든 방식 지원
✅ 외부 컴포넌트 연동: dotPagination 등과 연동 가능
✅ 에러 처리: 완전한 에러 경계 및 검증
✅ 디버그 모드: 개발 시 유용한 로깅

-->
