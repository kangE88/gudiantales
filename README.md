<!-- components/SCSwiper.vue -->
<template>
    <div 
      :class="`sc-swiper-container sc-swiper-${swiperId}`"
      :role="ariaRole"
      :aria-label="computedAriaLabel"
    >
      <!-- Swiper 컨테이너 -->
      <swiper 
        ref="swiperRef"
        :class="`swiper swiper-${swiperId}`"
        v-bind="swiperConfig"
        @swiper="onSwiperInit"
        @slideChange="onSlideChange"
        @progress="onProgress"
      >
        <!-- 데이터 기반 슬라이드 렌더링 (slides prop 사용 시) -->
        <template v-if="props.slides && props.slides.length > 0">
          <swiper-slide 
            v-for="(slide, index) in props.slides" 
            :key="slide.id || index"
          >
            <slot name="slide" :item="slide" :index="index">
              <!-- 기본 슬라이드 템플릿 -->
              <div class="sc-swiper-slide-default">
                <h3 v-if="slide.title">{{ slide.title }}</h3>
                <p v-if="slide.description">{{ slide.description }}</p>
                <img v-if="slide.image" :src="slide.image" :alt="slide.title || `Slide ${index + 1}`" />
              </div>
            </slot>
          </swiper-slide>
        </template>
        
        <!-- 템플릿 기반 슬라이드 (SwiperSlide 직접 사용 시) -->
        <template v-else>
          <slot />
        </template>
      </swiper>
      
      <!-- Pagination -->
      <div 
        v-if="shouldShowPagination" 
        :class="`swiper-pagination swiper-pagination-${swiperId}`"
      ></div>
      
      <!-- Navigation -->
      <div 
        v-if="shouldShowNavigation"
        :class="`swiper-button-prev swiper-button-prev-${swiperId}`"
      ></div>
      <div 
        v-if="shouldShowNavigation"
        :class="`swiper-button-next swiper-button-next-${swiperId}`"
      ></div>
      
      <!-- Scrollbar -->
      <div 
        v-if="shouldShowScrollbar" 
        :class="`swiper-scrollbar swiper-scrollbar-${swiperId}`"
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
    onUnmounted,
    onErrorCaptured, 
    shallowRef, 
    watchEffect,
    nextTick,
    markRaw
  } from 'vue';
  import { Swiper, SwiperSlide } from 'swiper/vue';
  
  // Swiper CSS import
  import 'swiper/css';
  import 'swiper/css/navigation';
  import 'swiper/css/pagination';
  import 'swiper/css/scrollbar';
  import 'swiper/css/effect-fade';
  import 'swiper/css/effect-cube';
  import 'swiper/css/effect-coverflow';
  import 'swiper/css/effect-flip';
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
  
  export interface ControllerConfig {
    control?: any; // 제어할 다른 Swiper 인스턴스
    inverse?: boolean; // 역방향 제어
    by?: 'slide' | 'container'; // 제어 방식
  }
  
  export interface SCSwiperProps {
    // 데이터 기반 props (ScSwiper용)
    slides?: any[];
    
    // 공통 Swiper 설정
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
    
    // Controller 기반 다중 Swiper 관리
    controller?: boolean | ControllerConfig;
    controllerGroup?: string; // 같은 그룹의 Swiper들끼리 연동
    
    // 기존 복잡한 ID 시스템 단순화
    swiperId?: string; // 간단한 ID만 지원
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
  
  // 공통 모듈 설정 팩토리 함수
  const createModuleConfig = <T>(
    config: boolean | T,
    baseConfig: T
  ): T | false => {
    if (config === false) return false;
    if (config === true) return baseConfig;
    return { ...baseConfig, ...config };
  };
  
  const createPaginationConfig = (
    pagination: boolean | PaginationType | PaginationConfig,
    paginationType: PaginationType | undefined,
    elementSelector: string
  ): PaginationConfig | false => {
    if (pagination === false) return false;
    
    const baseConfig: PaginationConfig = {
      el: elementSelector,
      clickable: true,
    };
    
    // 디버그 로그 추가
    if (props.debug) {
      console.log(`[createPaginationConfig] pagination:`, pagination);
      console.log(`[createPaginationConfig] paginationType:`, paginationType);
    }
    
    // 1. pagination이 객체인 경우 (가장 우선순위)
    if (typeof pagination === 'object' && pagination !== null) {
      const config = { ...baseConfig, ...pagination };
      if (props.debug) console.log(`[createPaginationConfig] Object config:`, config);
      return config;
    }
    
    // 2. pagination이 문자열인 경우
    if (typeof pagination === 'string') {
      const config = { ...baseConfig, type: pagination };
      if (props.debug) console.log(`[createPaginationConfig] String config:`, config);
      return config;
    }
    
    // 3. paginationType이 명시된 경우 (이게 우리의 주요 케이스)
    if (paginationType) {
      const config = { ...baseConfig, type: paginationType };
      if (props.debug) console.log(`[createPaginationConfig] PaginationType config:`, config);
      return config;
    }
    
    // 4. pagination이 true인 경우 (기본값)
    if (pagination === true) {
      const config = { ...baseConfig, type: 'bullets' };
      if (props.debug) console.log(`[createPaginationConfig] Default config:`, config);
      return config;
    }
    
    return false;
  };
  
  const createNavigationConfig = (
    navigation: boolean | NavigationConfig,
    uniqueId: string
  ): NavigationConfig | false => {
    const baseConfig: NavigationConfig = {
      nextEl: `.swiper-button-next-${uniqueId}`,
      prevEl: `.swiper-button-prev-${uniqueId}`
    };
    
    return createModuleConfig(navigation, baseConfig);
  };
  
  const createScrollbarConfig = (
    scrollbar: boolean | ScrollbarConfig,
    uniqueId: string
  ): ScrollbarConfig | false => {
    const baseConfig: ScrollbarConfig = {
      el: `.swiper-scrollbar-${uniqueId}`,
      draggable: true
    };
    
    return createModuleConfig(scrollbar, baseConfig);
  };
  
  const createControllerConfig = (
    controller: boolean | ControllerConfig,
    controllerGroup?: string,
    currentInstance?: any
  ): ControllerConfig | false => {
    if (controller === false) return false;
    
    const baseConfig: ControllerConfig = {
      by: 'slide',
      inverse: false
    };
    
    if (controller === true) {
      // controllerGroup이 있으면 그룹 내 다른 인스턴스들과 연동
      if (controllerGroup && currentInstance) {
        const groupInstances = controllerGroups.get(controllerGroup) || [];
        const otherInstances = groupInstances.filter(instance => instance !== currentInstance);
        if (otherInstances.length > 0) {
          baseConfig.control = otherInstances;
        }
      }
      return baseConfig;
    }
    
    return { ...baseConfig, ...controller };
  };
  
  const validateSwiperProps = (props: SCSwiperProps): void => {
    // 기본 검증 (항상 실행)
    if (typeof props.slidesPerView === 'number' && props.slidesPerView <= 0) {
      throw new Error('[SCSwiper] slidesPerView must be positive number');
    }
    
    if (typeof props.speed === 'number' && props.speed < 0) {
      throw new Error('[SCSwiper] speed must be non-negative');
    }
    
    if (typeof props.spaceBetween === 'number' && props.spaceBetween < 0) {
      throw new Error('[SCSwiper] spaceBetween must be non-negative');
    }
    
    // 디버그 모드 추가 검증
    if (props.debug) {
      console.log('[SCSwiper] Props validation passed:', {
        slidesPerView: props.slidesPerView,
        speed: props.speed,
        spaceBetween: props.spaceBetween
      });
    }
  };
  
  // 모듈 캐시 (성능 최적화)
  const moduleCache = markRaw({
    Pagination: null as any,
    Navigation: null as any,
    Scrollbar: null as any,
    Autoplay: null as any,
    Controller: null as any, // Controller 모듈 추가
    EffectFade: null as any,
    EffectCube: null as any,
    EffectCoverflow: null as any,
    EffectFlip: null as any,
  });
  
  // Controller 그룹 관리 (전역)
  const controllerGroups = markRaw(new Map<string, any[]>());
  
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
    
    if (props.controller && !moduleCache.Controller) {
      const { Controller } = await import('swiper/modules');
      moduleCache.Controller = markRaw(Controller);
    }
    if (props.controller) modules.push(moduleCache.Controller);
    
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
    slides: () => [],
    pagination: true,
    paginationType: undefined,
    navigation: true,
    scrollbar: false,
    autoplay: false,
    controller: false,
    controllerGroup: undefined,
    loop: false,
    slidesPerView: 1,
    spaceBetween: 0,
    centeredSlides: false,
    direction: 'horizontal',
    speed: 300,
    effect: 'slide',
    swiperId: undefined,
    debug: true,
    exposeInstance: false,
  });
  
  // Emits 정의
  const emit = defineEmits<{
    slideChange: [{ activeIndex: number, realIndex: number }];
    progress: [{ progress: number }];
    init: [any];
    error: [Error];
    beforeSlideChange: [{ from: number, to: number }];
    afterSlideChange: [{ activeIndex: number }];
    reachEnd: [];
    reachBeginning: [];
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
  
  // 간단한 ID 시스템 (Controller 기반으로 단순화)
  const swiperId = computed(() => 
    props.swiperId || generateUniqueId('swiper')
  );
  
  // 접근성 속성
  const ariaRole = computed(() => 'region');
  const computedAriaLabel = computed(() => 
    props.ariaLabel || `Swiper carousel with ${totalSlides.value} slides`
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
      `.swiper-pagination-${swiperId.value}`
    )
  );
  
  const navigationConfig = computed(() => 
    createNavigationConfig(props.navigation, swiperId.value)
  );
  
  const scrollbarConfig = computed(() => 
    createScrollbarConfig(props.scrollbar, swiperId.value)
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
  
  // Controller 설정 (새로 추가)
  const controllerConfig = computed(() => 
    createControllerConfig(
      props.controller || false,
      props.controllerGroup,
      swiperInstance.value
    )
  );
  
  // 최종 Swiper 설정 (Controller 기반으로 단순화)
  const swiperConfig = computed(() => ({
    modules: modules.value,
    pagination: paginationConfig.value,
    navigation: navigationConfig.value,
    scrollbar: scrollbarConfig.value,
    autoplay: autoplayConfig.value,
    controller: controllerConfig.value, // Controller 설정 추가
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
  
  // DOM 요소 연결을 위한 재시도 함수
  const connectElements = async (swiper: any, retryCount = 0) => {
    const maxRetries = 5;
    const retryDelay = 100;
    
    if (retryCount >= maxRetries) {
      console.warn(`[SCSwiper ${swiperId.value}] Max retries reached for DOM element connection`);
      return;
    }
    
    // Navigation 버튼 연결
    if (props.navigation !== false) {
      const nextEl = document.querySelector(`.swiper-button-next-${swiperId.value}`);
      const prevEl = document.querySelector(`.swiper-button-prev-${swiperId.value}`);
      
      if (nextEl && prevEl) {
        // Swiper Navigation 모듈 연결
        if (swiper.navigation) {
          swiper.navigation.nextEl = nextEl;
          swiper.navigation.prevEl = prevEl;
          swiper.navigation.init();
          swiper.navigation.update();
        }
        
        // 추가: 직접 DOM 이벤트 리스너 추가 (백업용)
        nextEl.addEventListener('click', (e) => {
          e.preventDefault();
          e.stopPropagation();
          if (props.debug) {
            console.log(`[SCSwiper ${swiperId.value}] Next button clicked (direct event)`);
          }
          swiper.slideNext();
        });
        
        prevEl.addEventListener('click', (e) => {
          e.preventDefault();
          e.stopPropagation();
          if (props.debug) {
            console.log(`[SCSwiper ${swiperId.value}] Prev button clicked (direct event)`);
          }
          swiper.slidePrev();
        });
        
        if (props.debug) {
          console.log(`[SCSwiper ${swiperId.value}] Navigation connected successfully on attempt ${retryCount + 1}`);
          console.log(`[SCSwiper ${swiperId.value}] Direct event listeners added to navigation buttons`);
        }
      } else {
        if (props.debug) {
          console.log(`[SCSwiper ${swiperId.value}] Navigation elements not ready, retrying... (${retryCount + 1}/${maxRetries})`);
        }
        setTimeout(() => connectElements(swiper, retryCount + 1), retryDelay);
        return;
      }
    }
    
    // Pagination 연결
    if (props.pagination !== false) {
      const paginationEl = document.querySelector(`.swiper-pagination-${swiperId.value}`);
      
      if (paginationEl) {
        // Swiper Pagination 모듈 연결 (강화된 버전)
        if (swiper.pagination) {
          // 1. 기존 pagination 제거
          if (swiper.pagination.el) {
            swiper.pagination.destroy();
          }
          
          // 2. 새로운 element 설정
          swiper.pagination.el = paginationEl;
          
          // 3. 재초기화
          swiper.pagination.init();
          swiper.pagination.render();
          swiper.pagination.update();
          
          if (props.debug) {
            console.log(`[SCSwiper ${swiperId.value}] Pagination module reconnected`);
          }
        } else {
          // pagination 모듈이 없는 경우 수동으로 설정
          if (props.debug) {
            console.log(`[SCSwiper ${swiperId.value}] No pagination module, manual setup required`);
          }
        }
        
        // pagination type별 특별 처리
        if (props.paginationType === 'fraction') {
          const updatePagination = () => {
            const current = swiper.realIndex + 1;
            const total = swiper.slides.length;
            paginationEl.innerHTML = `${current} / ${total}`;
          };
          
          // 초기 설정
          updatePagination();
          
          // 슬라이드 변경 시 업데이트
          swiper.on('slideChange', updatePagination);
        } else if (props.paginationType === 'bullets') {
          // bullets 타입의 경우 수동으로 bullets 생성
          const renderBullets = () => {
            const total = swiper.slides.length;
            const current = swiper.realIndex;
            
            let bulletsHTML = '';
            for (let i = 0; i < total; i++) {
              const activeClass = i === current ? ' swiper-pagination-bullet-active' : '';
              bulletsHTML += `<span class="swiper-pagination-bullet${activeClass}" data-index="${i}"></span>`;
            }
            paginationEl.innerHTML = bulletsHTML;
            
            // bullet 클릭 이벤트 추가
            paginationEl.querySelectorAll('.swiper-pagination-bullet').forEach((bullet, index) => {
              bullet.addEventListener('click', () => {
                swiper.slideTo(index);
              });
            });
          };
          
          // 초기 렌더링
          renderBullets();
          
          // 슬라이드 변경 시 업데이트
          swiper.on('slideChange', renderBullets);
          
          if (props.debug) {
            console.log(`[SCSwiper ${swiperId.value}] Manual bullets rendered`);
          }
        } else if (props.paginationType === 'progressbar') {
          // progressbar 타입 수동 구현
          const renderProgressbar = () => {
            const progress = (swiper.realIndex + 1) / swiper.slides.length * 100;
            paginationEl.innerHTML = `
              <span class="swiper-pagination-progressbar-fill" style="transform: translateX(${progress - 100}%);"></span>
            `;
          };
          
          // 초기 렌더링
          renderProgressbar();
          
          // 슬라이드 변경 시 업데이트
          swiper.on('slideChange', renderProgressbar);
          
          if (props.debug) {
            console.log(`[SCSwiper ${swiperId.value}] Manual progressbar rendered`);
          }
        } else if (props.paginationType === 'custom') {
          // custom 타입의 경우 사용자 정의 렌더링
          const renderCustomPagination = () => {
            const current = swiper.realIndex + 1;
            const total = swiper.slides.length;
            paginationEl.innerHTML = `
              <div style="display: flex; gap: 8px; align-items: center;">
                <span style="background: #007aff; color: white; padding: 4px 8px; border-radius: 12px; font-size: 12px;">
                  ${current}
                </span>
                <span style="color: #666; font-size: 12px;">of</span>
                <span style="background: #e0e0e0; color: #333; padding: 4px 8px; border-radius: 12px; font-size: 12px;">
                  ${total}
                </span>
              </div>
            `;
          };
          
          // 초기 설정
          renderCustomPagination();
          
          // 슬라이드 변경 시 업데이트
          swiper.on('slideChange', renderCustomPagination);
        }
        
        if (props.debug) {
          console.log(`[SCSwiper ${swiperId.value}] Pagination connected successfully on attempt ${retryCount + 1}`);
          console.log(`[SCSwiper ${swiperId.value}] Pagination type: ${props.paginationType}`);
          console.log(`[SCSwiper ${swiperId.value}] Pagination config:`, swiper.params.pagination);
          console.log(`[SCSwiper ${swiperId.value}] Pagination element innerHTML:`, paginationEl.innerHTML);
        }
      } else {
        if (props.debug) {
          console.log(`[SCSwiper ${swiperId.value}] Pagination element not ready, retrying... (${retryCount + 1}/${maxRetries})`);
        }
        setTimeout(() => connectElements(swiper, retryCount + 1), retryDelay);
        return;
      }
    }
  };

  // 이벤트 핸들러들
  const onSwiperInit = async (swiper: any) => {
    swiperInstance.value = swiper;
    totalSlides.value = swiper.slides.length;
    
    if (props.debug) {
      console.log(`[SCSwiper ${swiperId.value}] Swiper initialized, connecting elements...`);
    }
    
    // DOM 요소 연결 (재시도 로직 포함)
    await nextTick();
    connectElements(swiper);
    
    // Controller 그룹에 추가
    if (props.controllerGroup) {
      const groupInstances = controllerGroups.get(props.controllerGroup) || [];
      groupInstances.push(swiper);
      controllerGroups.set(props.controllerGroup, groupInstances);
      
      // 기존 그룹 멤버들과 상호 연결
      groupInstances.forEach(instance => {
        if (instance !== swiper && instance.controller) {
          const otherInstances = groupInstances.filter(i => i !== instance);
          instance.controller.control = otherInstances;
        }
      });
    }
    
    emit('init', swiper);
    
    if (props.debug) {
      console.log(`[SCSwiper ${swiperId.value}] Initialized with ${totalSlides.value} slides`);
      console.log(`[SCSwiper ${swiperId.value}] Loaded modules:`, modules.value);
      console.log(`[SCSwiper ${swiperId.value}] Navigation config:`, navigationConfig.value);
      console.log(`[SCSwiper ${swiperId.value}] Pagination config:`, paginationConfig.value);
      if (props.controllerGroup) {
        console.log(`[SCSwiper ${swiperId.value}] Added to controller group: ${props.controllerGroup}`);
      }
    }
  };
  
  const onSlideChange = (swiper: any) => {
    const previousIndex = currentSlideIndex.value;
    currentSlideIndex.value = swiper.activeIndex;
    isAtStart.value = swiper.isBeginning;
    isAtEnd.value = swiper.isEnd;
    
    // 이벤트 발생
    emit('slideChange', {
      activeIndex: swiper.activeIndex,
      realIndex: swiper.realIndex
    });
    
    emit('afterSlideChange', { activeIndex: swiper.activeIndex });
    
    // 시작/끝 도달 이벤트
    if (swiper.isBeginning) {
      emit('reachBeginning');
    }
    if (swiper.isEnd) {
      emit('reachEnd');
    }
  };
  
  const onProgress = (swiper: any, progress: number) => {
    scrollProgress.value = Math.round(progress * 100);
    emit('progress', { progress });
  };
  
  // 에러 처리
  onErrorCaptured((error) => {
    console.error(`[SCSwiper ${swiperId.value}] Error:`, error);
    emit('error', error);
    return false;
  });
  
  // 초기화
  onMounted(async () => {
    try {
      validateSwiperProps(props);
      modules.value = await getRequiredModules(props);
      
      // DOM 요소가 완전히 렌더링될 때까지 기다림
      await nextTick();
      await nextTick(); // 추가 틱으로 안정성 확보
      
      if (props.debug) {
        console.log(`[SCSwiper ${swiperId.value}] Mounted with modules:`, modules.value);
        console.log(`[SCSwiper ${swiperId.value}] Config:`, swiperConfig.value);
        console.log(`[SCSwiper ${swiperId.value}] Pagination config:`, paginationConfig.value);
        console.log(`[SCSwiper ${swiperId.value}] Navigation config:`, navigationConfig.value);
        
        // DOM 요소 확인
        const nextEl = document.querySelector(`.swiper-button-next-${swiperId.value}`);
        const prevEl = document.querySelector(`.swiper-button-prev-${swiperId.value}`);
        const paginationEl = document.querySelector(`.swiper-pagination-${swiperId.value}`);
        
        console.log(`[SCSwiper ${swiperId.value}] DOM Elements at mount:`, {
          nextEl: nextEl ? 'found' : 'not found',
          prevEl: prevEl ? 'found' : 'not found',
          paginationEl: paginationEl ? 'found' : 'not found'
        });
      }
    } catch (error) {
      console.error(`[SCSwiper ${swiperId.value}] Mount error:`, error);
      emit('error', error as Error);
    }
  });
  
  // 메모리 누수 방지
  onUnmounted(() => {
    if (swiperInstance.value) {
      try {
        // Controller 그룹에서 제거
        if (props.controllerGroup) {
          const groupInstances = controllerGroups.get(props.controllerGroup) || [];
          const updatedInstances = groupInstances.filter(instance => instance !== swiperInstance.value);
          
          if (updatedInstances.length === 0) {
            controllerGroups.delete(props.controllerGroup);
          } else {
            controllerGroups.set(props.controllerGroup, updatedInstances);
            // 남은 인스턴스들 간의 연결 재설정
            updatedInstances.forEach(instance => {
              if (instance.controller) {
                const otherInstances = updatedInstances.filter(i => i !== instance);
                instance.controller.control = otherInstances;
              }
            });
          }
        }
        
        swiperInstance.value.destroy(true, true);
        if (props.debug) {
          console.log(`[SCSwiper ${swiperId.value}] Destroyed`);
        }
      } catch (error) {
        console.error(`[SCSwiper ${swiperId.value}] Destroy error:`, error);
      }
    }
  });
  
  // Props 변경 감지 (성능 최적화)
  watchEffect(() => {
    if (swiperInstance.value) {
      swiperInstance.value.update();
    }
  });
  
  // 디버그용 네비게이션 테스트 함수
  const testNavigation = () => {
    if (swiperInstance.value) {
      console.log(`[SCSwiper ${swiperId.value}] Testing navigation...`);
      console.log('Current slide:', swiperInstance.value.activeIndex);
      console.log('Total slides:', swiperInstance.value.slides.length);
      console.log('Navigation object:', swiperInstance.value.navigation);
      
      // 수동으로 다음 슬라이드로 이동
      swiperInstance.value.slideNext();
    }
  };
  
  // 외부에서 접근 가능한 메서드들
  defineExpose({
    swiper: swiperInstance,
    slideTo: (index: number) => swiperInstance.value?.slideTo(index),
    slideNext: () => swiperInstance.value?.slideNext(),
    slidePrev: () => swiperInstance.value?.slidePrev(),
    update: () => swiperInstance.value?.update(),
    testNavigation, // 디버그용 함수 추가
    
    // 추가: 외부 컴포넌트에서 필요한 상태들
    currentSlideIndex: computed(() => currentSlideIndex.value),
    totalSlides: computed(() => totalSlides.value),
    isAtStart: computed(() => isAtStart.value),
    isAtEnd: computed(() => isAtEnd.value),
    uniqueId: computed(() => swiperId.value),
  });
  </script>
  
  <style scoped>
  .sc-swiper-container {
    position: relative;
    width: 100%;
    min-height: 300px; /* 최소 높이 설정 */
  }

  .sc-swiper-container .swiper {
    width: 100%;
    height: 100%;
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
  
  /* 기본 슬라이드 스타일 */
  .sc-swiper-slide-default {
    padding: 20px;
    text-align: center;
    background: #f8f9fa;
    border-radius: 8px;
  }
  
  .sc-swiper-slide-default h3 {
    margin: 0 0 12px 0;
    font-size: 18px;
    font-weight: 600;
    color: #333;
  }
  
  .sc-swiper-slide-default p {
    margin: 0 0 16px 0;
    font-size: 14px;
    color: #666;
    line-height: 1.5;
  }
  
  .sc-swiper-slide-default img {
    max-width: 100%;
    height: auto;
    border-radius: 4px;
  }
  
  /* ===== SWIPER 기본 스타일 ===== */
  /* Navigation 버튼 */
  :deep(.swiper-button-next),
  :deep(.swiper-button-prev) {
    display: flex !important;
    align-items: center !important;
    justify-content: center !important;
    width: 44px !important;
    height: 44px !important;
    margin-top: -22px !important;
    color: #007aff !important;
    font-weight: 900 !important;
    background: rgba(255, 255, 255, 0.9) !important;
    border-radius: 50% !important;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15) !important;
    transition: all 0.3s ease !important;
    z-index: 10 !important;
    opacity: 1 !important;
    visibility: visible !important;
  }
  
  :deep(.swiper-button-next:hover),
  :deep(.swiper-button-prev:hover) {
    background: rgba(255, 255, 255, 1) !important;
    transform: scale(1.1) !important;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2) !important;
  }

  :deep(.swiper-button-next.swiper-button-disabled),
  :deep(.swiper-button-prev.swiper-button-disabled) {
    opacity: 0.3 !important;
    cursor: not-allowed !important;
  }

  :deep(.swiper-button-next::after),
  :deep(.swiper-button-prev::after) {
    font-size: 18px !important;
    font-weight: 900 !important;
  }

  /* Pagination */
  :deep(.swiper-pagination) {
    display: block !important;
    bottom: 10px !important;
    z-index: 10 !important;
    opacity: 1 !important;
    visibility: visible !important;
  }

  :deep(.swiper-pagination-bullet) {
    width: 12px !important;
    height: 12px !important;
    background: rgba(0, 0, 0, 0.3) !important;
    opacity: 1 !important;
    margin: 0 4px !important;
    transition: all 0.3s ease !important;
  }

  :deep(.swiper-pagination-bullet-active) {
    background: #007aff !important;
    transform: scale(1.2) !important;
  }

  /* Pagination fraction */
  :deep(.swiper-pagination-fraction) {
    display: block !important;
    position: absolute !important;
    bottom: 10px !important;
    background: rgba(0, 0, 0, 0.7) !important;
    color: white !important;
    padding: 6px 12px !important;
    border-radius: 12px !important;
    font-size: 14px !important;
    font-weight: 500 !important;
    width: auto !important;
    left: 50% !important;
    transform: translateX(-50%) !important;
    z-index: 10 !important;
    opacity: 1 !important;
    visibility: visible !important;
    text-align: center !important;
  }

  /* Pagination progressbar */
  :deep(.swiper-pagination-progressbar),
  :deep(.swiper-pagination[data-type="progressbar"]) {
    position: relative !important;
    background: rgba(0, 0, 0, 0.1) !important;
    height: 4px !important;
    border-radius: 2px !important;
    overflow: hidden !important;
  }

  :deep(.swiper-pagination-progressbar-fill) {
    position: absolute !important;
    left: 0 !important;
    top: 0 !important;
    width: 100% !important;
    height: 100% !important;
    background: #007aff !important;
    border-radius: 2px !important;
    transform-origin: left center !important;
    transition: transform 0.3s ease !important;
  }

  /* Scrollbar */
  :deep(.swiper-scrollbar) {
    background: rgba(0, 0, 0, 0.1) !important;
    border-radius: 4px !important;
  }

  :deep(.swiper-scrollbar-drag) {
    background: #007aff !important;
    border-radius: 4px !important;
  }
  
  /* 반응형 */
  @media (max-width: 768px) {
    :deep(.swiper-button-next),
    :deep(.swiper-button-prev) {
      width: 36px !important;
      height: 36px !important;
      margin-top: -18px !important;
    }
    
    :deep(.swiper-button-next::after),
    :deep(.swiper-button-prev::after) {
      font-size: 14px !important;
    }
  }
  </style>
