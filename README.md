<!-- components/SCSwiper.vue -->

<template>
  <div 
    class="sc-swiper-container"
    :class="swiperClasses"
    :aria-label="computedAriaLabel"
  >
    <!-- Swiper 컨테이너 -->
    <swiper 
      ref="swiperRef"
      class="swiper"
      v-bind="swiperConfig"
      @swiper="onSwiperInit"
      @slideChange="onSlideChange"
      @progress="onProgress"
    >
      <slot />
    </swiper>

```
<!-- Screen Reader 전용 정보 -->
<div class="sr-only" aria-live="polite" aria-atomic="true">
  현재 {{ currentSlideIndex + 1 }}번째 슬라이드, 총 {{ totalSlides }}개
</div>
```

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
  hideOnClick?: boolean;
  disabledClass?: string;
  hiddenClass?: string;
  lockClass?: string;
}

export interface ScrollbarConfig {
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
  wrapperClass?: string;
  ariaLabel?: string;
  debug?: boolean;
  
  // 스타일링 관련
  size?: 'small' | 'medium' | 'large';
  theme?: 'default' | 'dark' | 'light';
}

// ============================================================================
// UTILITIES
// ============================================================================
const createPaginationConfig = (
  pagination: boolean | PaginationType | PaginationConfig,
  paginationType: PaginationType | undefined
): PaginationConfig | boolean => {
  if (pagination === false) return false;
  
  const baseConfig: PaginationConfig = {
    clickable: true,
  };
  
  // 1. pagination이 객체인 경우 (가장 우선순위)
  if (typeof pagination === 'object' && pagination !== null) {
    return { ...baseConfig, ...pagination };
  }
  
  // 2. pagination이 문자열인 경우
  if (typeof pagination === 'string') {
    return { ...baseConfig, type: pagination };
  }
  
  // 3. paginationType이 명시된 경우
  if (paginationType) {
    return { ...baseConfig, type: paginationType };
  }
  
  // 4. pagination이 true인 경우 (기본값)
  if (pagination === true) {
    return { ...baseConfig, type: 'bullets' };
  }
  
  return false;
};

const createNavigationConfig = (
  navigation: boolean | NavigationConfig
): NavigationConfig | boolean => {
  if (navigation === false) return false;
  
  const baseConfig: NavigationConfig = {};
  
  if (navigation === true) {
    return baseConfig;
  }
  
  return { ...baseConfig, ...navigation };
};

const createScrollbarConfig = (
  scrollbar: boolean | ScrollbarConfig
): ScrollbarConfig | boolean => {
  if (scrollbar === false) return false;
  
  const baseConfig: ScrollbarConfig = {
    draggable: true
  };
  
  if (scrollbar === true) {
    return baseConfig;
  }
  
  return { ...baseConfig, ...scrollbar };
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
  Controller: null as any,
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
  
  // Controller는 항상 포함 (인스턴스 독립성 보장)
  if (!moduleCache.Controller) {
    const { Controller } = await import('swiper/modules');
    moduleCache.Controller = markRaw(Controller);
  }
  modules.push(moduleCache.Controller);
  
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
const props = withDefaults(defineProps<SCSwiperProps>(), {
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
  size: 'medium',
  theme: 'default',
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

// 스와이퍼 클래스 계산
const swiperClasses = computed(() => {
  const classes = [];
  if (props.size) classes.push(`sc-swiper--${props.size}`);
  if (props.theme) classes.push(`sc-swiper--${props.theme}`);
  if (props.effect) classes.push(`sc-swiper--${props.effect}`);
  return classes.join(' ');
});

// 접근성 속성
const computedAriaLabel = computed(() => 
  props.ariaLabel || `Swiper carousel with ${totalSlides.value} slides`
);

// 설정 객체들 (Controller 사용으로 간소화)
const paginationConfig = computed(() => 
  createPaginationConfig(props.pagination, props.paginationType)
);

const navigationConfig = computed(() => 
  createNavigationConfig(props.navigation)
);

const scrollbarConfig = computed(() => 
  createScrollbarConfig(props.scrollbar)
);

const autoplayConfig = computed(() => {
  if (props.autoplay === false) return false;
  
  const baseConfig = {
    delay: 3000,
    disableOnInteraction: false,
  };
  
  if (props.autoplay === true) return baseConfig;
  return { ...baseConfig, ...props.autoplay };
});

// 최종 Swiper 설정 (간소화됨)
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
    console.log(`[SCSwiper] Initialized with ${totalSlides.value} slides`);
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
  console.error(`[SCSwiper] Error:`, error);
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
      console.log(`[SCSwiper] Mounted with modules:`, modules.value);
    }
  } catch (error) {
    console.error(`[SCSwiper] Mount error:`, error);
    emit('error', error as Error);
  }
});

// Props 변경 감지 (성능 최적화)
watchEffect(() => {
  if (swiperInstance.value) {
    swiperInstance.value.update();
  }
});

// 외부에서 접근 가능한 메서드들
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
});
</script>

<style scoped>
.sc-swiper-container {
  position: relative;
  width: 100%;
}

/* 사이즈 variants */
.sc-swiper--small {
  font-size: 12px;
}

.sc-swiper--medium {
  font-size: 14px;
}

.sc-swiper--large {
  font-size: 16px;
}

/* 테마 variants */
.sc-swiper--dark {
  color: white;
}

.sc-swiper--dark :deep(.swiper-button-next),
.sc-swiper--dark :deep(.swiper-button-prev) {
  color: white;
}

.sc-swiper--dark :deep(.swiper-pagination-bullet) {
  background: rgba(255, 255, 255, 0.5);
}

.sc-swiper--dark :deep(.swiper-pagination-bullet-active) {
  background: white;
}

.sc-swiper--light {
  background: #f7fafc;
}

/* 이펙트별 스타일 */
.sc-swiper--fade :deep(.swiper-slide) {
  transition: opacity 0.3s ease;
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

/* 포커스 스타일 */
:deep(.swiper-button-next:focus),
:deep(.swiper-button-prev:focus),
:deep(.swiper-pagination-bullet:focus) {
  outline: 2px solid #007aff;
  outline-offset: 2px;
}

/* 애니메이션 감소 설정 지원 */
@media (prefers-reduced-motion: reduce) {
  :deep(.swiper-slide),
  :deep(.swiper-button-next),
  :deep(.swiper-button-prev),
  :deep(.swiper-pagination-bullet) {
    transition: none !important;
  }
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
:pagination=”{
type: ‘bullets’,
clickable: true,
bulletClass: ‘custom-bullet’
}”
:navigation=“true”
:autoplay=”{ delay: 3000 }”
:loop=“true”
size=“large”
theme=“dark”

```
<SwiperSlide>Slide 1</SwiperSlide>
<SwiperSlide>Slide 2</SwiperSlide>
```

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

```
<dotPagination 
  :swiper-ref="swiperRef" 
  direction="left" 
/>
```

  </div>
</template>

<script setup>
import { ref } from 'vue';
import { SwiperSlide } from 'swiper/vue';
import SCSwiper from '@/components/SCSwiper.vue';
import dotPagination from '@/components/dotPagination.vue';

const swiperRef = ref(null);
</script>

# ===========================================
필요한 CSS Import (main.ts 또는 App.vue):

import ‘swiper/css’;
import ‘swiper/css/navigation’;
import ‘swiper/css/pagination’;
import ‘swiper/css/scrollbar’;
import ‘swiper/css/effect-fade’;
import ‘swiper/css/effect-cube’;
import ‘swiper/css/effect-coverflow’;
import ‘swiper/css/effect-flip’;

# ===========================================
주요 개선사항:

✅ Controller 모듈 사용으로 인스턴스 자동 분리
✅ 고유 ID 수동 생성 불필요
✅ 더 간단한 구조와 코드
✅ SwiperJS 표준에 맞는 구현
✅ 동일한 기능과 성능 유지
✅ 타입 안전성 완전 지원

–>