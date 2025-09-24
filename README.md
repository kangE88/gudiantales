import { ScSwiper } from "@shc-nss/ui/shc";
import type { Meta, StoryObj } from "@storybook/vue3";

// 예시 슬라이드 데이터
const mockSlides = [
  {
    id: 1,
    title: "슬라이드 1",
    description: "첫 번째 슬라이드입니다.",
    image: "https://via.placeholder.com/600x300/ff6b6b/ffffff?text=Slide+1",
  },
  {
    id: 2,
    title: "슬라이드 2",
    description: "두 번째 슬라이드입니다.",
    image: "https://via.placeholder.com/600x300/4ecdc4/ffffff?text=Slide+2",
  },
  {
    id: 3,
    title: "슬라이드 3",
    description: "세 번째 슬라이드입니다.",
    image: "https://via.placeholder.com/600x300/45b7d1/ffffff?text=Slide+3",
  },
  {
    id: 4,
    title: "슬라이드 4",
    description: "네 번째 슬라이드입니다.",
    image: "https://via.placeholder.com/600x300/96ceb4/ffffff?text=Slide+4",
  },
  {
    id: 5,
    title: "슬라이드 5",
    description: "다섯 번째 슬라이드입니다.",
    image: "https://via.placeholder.com/600x300/feca57/ffffff?text=Slide+5",
  },
];

const meta: Meta<typeof ScSwiper> = {
  title: "SHC/ScSwiper",
  component: ScSwiper,
  parameters: {
    layout: "padded",
  },
  args: {
    slides: mockSlides,
    slidesPerView: 1,
    spaceBetween: 16,
    pagination: true,
    paginationType: "bullets",
    navigation: true,
    controls: true,
    keyboard: true,
    autoplay: false,
    size: "medium",
    theme: "default",
    effect: "slide",
  },
  argTypes: {
    size: {
      control: "radio",
      options: ["small", "medium", "large", "xlarge"],
      table: {
        type: { summary: `"small" | "medium" | "large" | "xlarge"` },
      },
    },
    theme: {
      control: "radio",
      options: ["default", "dark", "light"],
      table: {
        type: { summary: `"default" | "dark" | "light"` },
      },
    },
    effect: {
      control: "radio",
      options: ["slide", "fade", "cube", "coverflow", "flip"],
      table: {
        type: { summary: `"slide" | "fade" | "cube" | "coverflow" | "flip"` },
      },
    },
    paginationType: {
      control: "radio",
      options: ["bullets", "fraction", "progressbar"],
      table: {
        type: { summary: `"bullets" | "fraction" | "progressbar"` },
      },
    },
    slidesPerView: {
      control: { type: "number", min: 1, max: 5, step: 1 },
      table: {
        type: { summary: "number" },
      },
    },
    spaceBetween: {
      control: { type: "number", min: 0, max: 50, step: 4 },
      table: {
        type: { summary: "number" },
      },
    },
    speed: {
      control: { type: "number", min: 100, max: 1000, step: 100 },
      table: {
        type: { summary: "number" },
      },
    },
    pagination: {
      control: "boolean",
    },
    navigation: {
      control: "boolean",
    },
    controls: {
      control: "boolean",
    },
    keyboard: {
      control: "boolean",
    },
    autoplay: {
      control: "boolean",
    },
    centered: {
      control: "boolean",
    },
    freeMode: {
      control: "boolean",
    },
    allowTouchMove: {
      control: "boolean",
    },
    loop: {
      control: "boolean",
    },
    autoplayOnStart: {
      control: "boolean",
    },
  },
};

export default meta;
type Story = StoryObj<typeof meta>;

// Vertical Direction Story
export const VerticalDirection: Story = {
  name: "세로 방향 (Vertical)",
  args: {
    direction: 'vertical',
    slidesPerView: 2,
    spaceBetween: 16,
    pagination: true,
    paginationType: 'bullets',
    navigation: true,
    height: '400px'
  },
  render: (args) => ({
    components: { ScSwiper },
    setup() {
      const slides = Array.from({ length: 6 }, (_, i) => ({
        n: i + 1,
        title: `세로 슬라이드 ${i + 1}`,
        content: `이것은 세로 방향 슬라이드입니다. 위아래 화살표 키나 마우스 드래그로 조작할 수 있습니다.`
      }));
      return { args: { ...args, slides } };
    },
    template: `
      <div style="height: 400px; border: 2px dashed #ccc; padding: 20px;">
        <h3>Vertical Swiper Test</h3>
        <ScSwiper v-bind="args">
          <template #slide="{ item, index }">
            <div style="
              padding: 16px;
              background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
              color: white;
              border-radius: 8px;
              text-align: center;
              min-height: 80px;
              display: flex;
              flex-direction: column;
              justify-content: center;
            ">
              <h4 style="margin: 0 0 8px; font-size: 18px;">{{ item.title }}</h4>
              <p style="margin: 0; font-size: 14px; opacity: 0.9;">{{ item.content }}</p>
            </div>
          </template>
        </ScSwiper>
      </div>
    `,
  }),
};

export const Default: Story = {
  render: (args: any) => ({
    components: { ScSwiper },
    setup() {
      return { args };
    },
    template: `
      <ScSwiper v-bind="args">
        <template #slide="{ item, index }">
          <div class="story-slide">
            <img :src="item.image" :alt="item.title" />
            <div class="slide-content">
              <h3>{{ item.title }}</h3>
              <p>{{ item.description }}</p>
            </div>
          </div>
        </template>
      </ScSwiper>
    `,
    styles: [
      `
      <style scoped>
      .story-slide {
        position: relative;
        border-radius: 8px;
        overflow: hidden;
        background: #f8f9fa;
      }
      .story-slide img {
        width: 100%;
        height: 200px;
        object-fit: cover;
      }
      .slide-content {
        padding: 16px;
      }
      .slide-content h3 {
        margin: 0 0 8px;
        font-size: 18px;
        font-weight: 600;
      }
      .slide-content p {
        margin: 0;
        color: #666;
        font-size: 14px;
      }
      </style>
    `,
    ],
  }),
};

export const MultipleSlides: Story = {
  args: {
    slidesPerView: 3,
    spaceBetween: 16,
    centered: false,
  },
  render: (args: any) => ({
    components: { ScSwiper },
    setup() {
      return { args };
    },
    template: `
      <ScSwiper v-bind="args">
        <template #slide="{ item, index }">
          <div class="story-slide-simple">
            <div class="slide-number">{{ index + 1 }}</div>
            <h4>{{ item.title }}</h4>
          </div>
        </template>
      </ScSwiper>
    `,
    styles: [
      `
      <style scoped>
      .story-slide-simple {
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        min-height: 150px;
        padding: 20px;
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        color: white;
        border-radius: 12px;
        text-align: center;
      }
      .slide-number {
        font-size: 32px;
        font-weight: bold;
        margin-bottom: 8px;
      }
      .story-slide-simple h4 {
        margin: 0;
        font-size: 16px;
      }
      </style>
    `,
    ],
  }),
};

export const FadeEffectWithAutoplay: Story = {
  args: {
    effect: "fade",
    autoplay: {
      delay: 2000,
      pauseOnInteraction: true,
    },
    speed: 500,
  },
  render: (args: any) => ({
    components: { ScSwiper },
    setup() {
      return { args };
    },
    template: `
      <ScSwiper v-bind="args">
        <template #slide="{ item, index }">
          <div 
            class="story-slide-fade"
            :style="{ background: getGradient(index) }"
          >
            <div class="fade-content">
              <h2>{{ item.title }}</h2>
              <p>{{ item.description }}</p>
            </div>
          </div>
        </template>
      </ScSwiper>
    `,
    methods: {
      getGradient(index: number) {
        const gradients = [
          "linear-gradient(135deg, #ff6b6b, #feca57)",
          "linear-gradient(135deg, #4ecdc4, #44a08d)",
          "linear-gradient(135deg, #45b7d1, #96ceb4)",
          "linear-gradient(135deg, #a8edea, #fed6e3)",
          "linear-gradient(135deg, #ffecd2, #fcb69f)",
        ];
        return gradients[index % gradients.length];
      },
    },
    styles: [
      `
      <style scoped>
      .story-slide-fade {
        display: flex;
        align-items: center;
        justify-content: center;
        min-height: 300px;
        border-radius: 16px;
      }
      .fade-content {
        text-align: center;
        color: white;
        text-shadow: 0 2px 4px rgba(0,0,0,0.3);
      }
      .fade-content h2 {
        margin: 0 0 16px;
        font-size: 32px;
        font-weight: 700;
      }
      .fade-content p {
        margin: 0;
        font-size: 18px;
        opacity: 0.9;
      }
      </style>
    `,
    ],
  }),
};

export const WithProgressbar: Story = {
  args: {
    paginationType: "progressbar",
    autoplay: {
      delay: 1500,
    },
    size: "large",
  },
  render: (args: any) => ({
    components: { ScSwiper },
    setup() {
      return { args };
    },
    template: `
      <ScSwiper v-bind="args">
        <template #slide="{ item, index }">
          <div class="story-slide-progress">
            <div class="progress-number">{{ index + 1 }}</div>
            <h3>{{ item.title }}</h3>
            <p>{{ item.description }}</p>
          </div>
        </template>
      </ScSwiper>
    `,
    styles: [
      `
      <style scoped>
      .story-slide-progress {
        background: white;
        border: 2px solid #e1e8ed;
        border-radius: 12px;
        padding: 32px;
        text-align: center;
        min-height: 200px;
        display: flex;
        flex-direction: column;
        justify-content: center;
      }
      .progress-number {
        font-size: 48px;
        font-weight: bold;
        color: #667eea;
        margin-bottom: 16px;
      }
      .story-slide-progress h3 {
        margin: 0 0 12px;
        color: #2c3e50;
      }
      .story-slide-progress p {
        margin: 0;
        color: #7f8c8d;
      }
      </style>
    `,
    ],
  }),
};

// Fade Effect Story
export const FadeEffect: Story = {
  args: {
    effect: "fade",
    slidesPerView: 1,
    speed: 600,
    pagination: true,
    navigation: true,
    autoplay: { delay: 3000, pauseOnInteraction: false },
  },
  render: (args: any) => ({
    components: { ScSwiper },
    setup() {
      return { args };
    },
    template: `
      <ScSwiper v-bind="args">
        <template #slide="{ item, index }">
          <div class="story-slide-fade">
            <div class="fade-background" :style="{ backgroundImage: \`url(\${item.image})\` }">
              <div class="fade-content">
                <h2>{{ item.title }}</h2>
                <p>{{ item.description }}</p>
              </div>
            </div>
          </div>
        </template>
      </ScSwiper>
    `,
    styles: [
      `
      <style scoped>
      .story-slide-fade {
        width: 100%;
        height: 400px;
        position: relative;
      }
      .fade-background {
        width: 100%;
        height: 100%;
        background-size: cover;
        background-position: center;
        border-radius: 12px;
        display: flex;
        align-items: center;
        justify-content: center;
      }
      .fade-content {
        background: rgba(0, 0, 0, 0.6);
        color: white;
        padding: 24px;
        border-radius: 8px;
        text-align: center;
        backdrop-filter: blur(4px);
      }
      .fade-content h2 {
        margin: 0 0 12px;
        font-size: 24px;
      }
      .fade-content p {
        margin: 0;
        font-size: 16px;
        opacity: 0.9;
      }
      </style>
    `,
    ],
  }),
};

// Autoplay with Controls Story
export const AutoplayWithControls: Story = {
  args: {
    autoplay: { delay: 2000, pauseOnInteraction: true },
    autoplayOnStart: true,
    controls: true,
    navigation: true,
    pagination: true,
    paginationType: "progressbar",
    pauseOnMouseEnter: true,
    loop: true,
  },
  render: (args: any) => ({
    components: { ScSwiper },
    setup() {
      return { args };
    },
    template: `
      <ScSwiper v-bind="args">
        <template #slide="{ item, index }">
          <div class="story-slide-autoplay">
            <div class="autoplay-content">
              <div class="autoplay-icon">⏯️</div>
              <h3>{{ item.title }}</h3>
              <p>{{ item.description }}</p>
              <small>마우스를 올리면 일시정지됩니다</small>
            </div>
          </div>
        </template>
      </ScSwiper>
    `,
    styles: [
      `
      <style scoped>
      .story-slide-autoplay {
        background: linear-gradient(45deg, #f093fb 0%, #f5576c 100%);
        color: white;
        border-radius: 12px;
        padding: 40px;
        text-align: center;
        min-height: 250px;
        display: flex;
        flex-direction: column;
        justify-content: center;
      }
      .autoplay-icon {
        font-size: 48px;
        margin-bottom: 16px;
      }
      .story-slide-autoplay h3 {
        margin: 0 0 12px;
        font-size: 24px;
      }
      .story-slide-autoplay p {
        margin: 0 0 16px;
        font-size: 16px;
        opacity: 0.9;
      }
      .story-slide-autoplay small {
        font-size: 12px;
        opacity: 0.7;
      }
      </style>
    `,
    ],
  }),
};
