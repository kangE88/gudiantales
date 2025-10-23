import type { Meta, StoryObj } from "@storybook/vue3";
import { ref } from "vue";
import ScKeypad from "./ScKeypad.vue";

const meta: Meta<typeof ScKeypad> = {
  title: "SHC/ScKeypad",
  component: ScKeypad,
  parameters: {
    layout: "centered",
    docs: {
      description: {
        component: `
가상 키패드 컴포넌트

## 주요 기능
- 🔢 **숫자 입력**: 0-9 숫자 및 00 버튼 지원
- 🔄 **재배열**: 보안을 위한 숫자 랜덤 재배열 기능
- 🎨 **테마**: 라이트/다크 테마 지원
- ♿ **접근성**: 스크린 리더 음성 안내 지원
- 📱 **반응형**: 모바일 최적화

## 사용 예시
\`\`\`vue
<template>
  <ScKeypad 
    v-model:values="inputValues"
    :isDarkTheme="false"
    :showRearrange="true"
    :maxLength="16"
    @number-click="handleNumberClick"
    @delete-click="handleDelete"
    @rearrange-click="handleRearrange"
  />
  <div>입력값: {{ inputValues }}</div>
</template>

<script setup>
import { ref } from 'vue';
import { ScKeypad } from '@shc-nss/ui/shc';

const inputValues = ref([]);

function handleNumberClick(value) {
  console.log('Number clicked:', value);
}

function handleDelete() {
  console.log('Delete clicked');
}

function handleRearrange(numbers) {
  console.log('Rearranged:', numbers);
}
</script>
\`\`\`
        `,
      },
    },
  },
  args: {
    showRearrange: false,
    isDarkTheme: false,
    maxLength: 16,
    initialMessage: "카드번호를 입력해주세요. 총 16자리입니다.",
    enableGroupAnnouncement: true,
  },
  argTypes: {
    showRearrange: {
      control: "boolean",
      description: "재배열 버튼 표시 여부 (다크 테마에서는 항상 표시)",
      table: {
        type: { summary: "boolean" },
        defaultValue: { summary: "false" },
      },
    },
    isDarkTheme: {
      control: "boolean",
      description: "다크 테마 사용 여부",
      table: {
        type: { summary: "boolean" },
        defaultValue: { summary: "false" },
      },
    },
    maxLength: {
      control: { type: "number", min: 1, max: 20, step: 1 },
      description: "최대 입력 자릿수",
      table: {
        type: { summary: "number" },
        defaultValue: { summary: "16" },
      },
    },
    initialMessage: {
      control: "text",
      description: "초기 음성 메시지",
      table: {
        type: { summary: "string" },
        defaultValue: { summary: "카드번호를 입력해주세요. 총 16자리입니다." },
      },
    },
    enableGroupAnnouncement: {
      control: "boolean",
      description: "4자리 단위로 그룹핑 안내 여부",
      table: {
        type: { summary: "boolean" },
        defaultValue: { summary: "true" },
      },
    },
  },
};

export default meta;
type Story = StoryObj<typeof meta>;

// ============================================================================
// 기본 스토리들
// ============================================================================

export const Default: Story = {
  parameters: {
    docs: {
      description: {
        story: "기본 키패드 구성입니다. v-model:values를 사용하여 입력값을 양방향 바인딩합니다.",
      },
    },
  },
  render: (args: any) => ({
    components: { ScKeypad },
    setup() {
      const inputValues = ref<string[]>([]);
      const eventLogs = ref<string[]>([]);

      const handleNumberClick = (value: string) => {
        eventLogs.value.unshift(`숫자 입력: ${value}`);
        if (eventLogs.value.length > 5) eventLogs.value.pop();
      };

      const handleDeleteClick = () => {
        eventLogs.value.unshift(`삭제`);
        if (eventLogs.value.length > 5) eventLogs.value.pop();
      };

      const handleRearrangeClick = (numbers: string[]) => {
        eventLogs.value.unshift(`재배열: [${numbers.join(", ")}]`);
        if (eventLogs.value.length > 5) eventLogs.value.pop();
      };

      return { args, inputValues, eventLogs, handleNumberClick, handleDeleteClick, handleRearrangeClick };
    },
    template: `
      <div style="max-width: 400px;">
        <div style="margin-bottom: 20px; padding: 16px; background: #f5f5f5; border-radius: 8px;">
          <div style="font-size: 14px; font-weight: 600; margin-bottom: 8px;">입력값 (v-model:values)</div>
          <div style="font-size: 24px; letter-spacing: 2px; font-family: monospace; min-height: 32px;">
            {{ inputValues.join('') || '&nbsp;' }}
          </div>
          <div style="font-size: 12px; color: #666; margin-top: 4px;">
            {{ inputValues.length }} / {{ args.maxLength }} 자리
          </div>
          <div style="font-size: 11px; color: #999; margin-top: 8px; font-family: monospace;">
            배열: [{{ inputValues.join(', ') }}]
          </div>
        </div>
        
        <ScKeypad 
          v-bind="args"
          v-model:values="inputValues"
          @number-click="handleNumberClick"
          @delete-click="handleDeleteClick"
          @rearrange-click="handleRearrangeClick"
        />
        
        <div style="margin-top: 20px; padding: 12px; background: #f9f9f9; border-radius: 8px; font-size: 12px;">
          <div style="font-weight: 600; margin-bottom: 8px;">이벤트 로그</div>
          <div v-for="(log, index) in eventLogs" :key="index" style="padding: 4px 0; color: #666;">
            {{ log }}
          </div>
          <div v-if="eventLogs.length === 0" style="color: #999;">
            키패드를 사용해보세요
          </div>
        </div>
      </div>
    `,
  }),
};

export const LightTheme: Story = {
  args: {
    isDarkTheme: false,
    showRearrange: false,
  },
  parameters: {
    docs: {
      description: {
        story: "라이트 테마 키패드입니다. 00 버튼이 표시됩니다.",
      },
    },
  },
  render: (args: any) => ({
    components: { ScKeypad },
    setup() {
      const inputValues = ref<string[]>([]);

      return { args, inputValues };
    },
    template: `
      <div style="max-width: 400px;">
        <div style="margin-bottom: 20px; padding: 16px; background: #f5f5f5; border-radius: 8px;">
          <div style="font-size: 14px; font-weight: 600; margin-bottom: 8px;">입력값</div>
          <div style="font-size: 24px; letter-spacing: 2px; font-family: monospace; min-height: 32px;">
            {{ inputValues.join('') || '&nbsp;' }}
          </div>
        </div>
        
        <ScKeypad 
          v-bind="args"
          v-model:values="inputValues"
        />
      </div>
    `,
  }),
};

export const DarkTheme: Story = {
  args: {
    isDarkTheme: true,
    showRearrange: true,
  },
  parameters: {
    docs: {
      description: {
        story: "다크 테마 키패드입니다. 재배열 버튼이 표시됩니다.",
      },
    },
  },
  render: (args: any) => ({
    components: { ScKeypad },
    setup() {
      const inputValue = ref("");
      const rearrangeCount = ref(0);

      const handleNumberClick = (value: string) => {
        inputValue.value += value;
      };

      const handleDeleteClick = () => {
        if (inputValue.value.length > 0) {
          inputValue.value = inputValue.value.slice(0, -1);
        }
      };

      const handleRearrangeClick = () => {
        rearrangeCount.value++;
      };

      return { args, inputValue, rearrangeCount, handleNumberClick, handleDeleteClick, handleRearrangeClick };
    },
    template: `
      <div style="max-width: 400px;">
        <div style="margin-bottom: 20px; padding: 16px; background: #1a1a1a; color: white; border-radius: 8px;">
          <div style="font-size: 14px; font-weight: 600; margin-bottom: 8px;">입력값</div>
          <div style="font-size: 24px; letter-spacing: 2px; font-family: monospace; min-height: 32px;">
            {{ inputValue || '&nbsp;' }}
          </div>
          <div style="font-size: 12px; color: #aaa; margin-top: 4px;">
            재배열 횟수: {{ rearrangeCount }}
          </div>
        </div>
        
        <ScKeypad 
          v-bind="args"
          @number-click="handleNumberClick"
          @delete-click="handleDeleteClick"
          @rearrange-click="handleRearrangeClick"
        />
      </div>
    `,
  }),
};

export const WithRearrangeButton: Story = {
  args: {
    isDarkTheme: false,
    showRearrange: true,
  },
  parameters: {
    docs: {
      description: {
        story: "재배열 버튼이 활성화된 라이트 테마 키패드입니다. 00 버튼 대신 재배열 버튼이 표시됩니다.",
      },
    },
  },
  render: (args: any) => ({
    components: { ScKeypad },
    setup() {
      const inputValue = ref("");
      const numberOrder = ref<string[]>([]);

      const handleNumberClick = (value: string) => {
        inputValue.value += value;
      };

      const handleDeleteClick = () => {
        if (inputValue.value.length > 0) {
          inputValue.value = inputValue.value.slice(0, -1);
        }
      };

      const handleRearrangeClick = (numbers: string[]) => {
        numberOrder.value = numbers;
      };

      return { args, inputValue, numberOrder, handleNumberClick, handleDeleteClick, handleRearrangeClick };
    },
    template: `
      <div style="max-width: 400px;">
        <div style="margin-bottom: 20px; padding: 16px; background: #f5f5f5; border-radius: 8px;">
          <div style="font-size: 14px; font-weight: 600; margin-bottom: 8px;">입력값</div>
          <div style="font-size: 24px; letter-spacing: 2px; font-family: monospace; min-height: 32px;">
            {{ inputValue || '&nbsp;' }}
          </div>
          <div v-if="numberOrder.length > 0" style="font-size: 12px; color: #666; margin-top: 8px;">
            현재 배열: {{ numberOrder.join(', ') }}
          </div>
        </div>
        
        <ScKeypad 
          v-bind="args"
          @number-click="handleNumberClick"
          @delete-click="handleDeleteClick"
          @rearrange-click="handleRearrangeClick"
        />
      </div>
    `,
  }),
};

export const CustomMaxLength: Story = {
  args: {
    isDarkTheme: false,
    showRearrange: false,
    maxLength: 6,
    initialMessage: "비밀번호 6자리를 입력해주세요.",
    enableGroupAnnouncement: false,
  },
  parameters: {
    docs: {
      description: {
        story: "최대 입력 길이를 6자리로 제한한 예시입니다. 비밀번호 입력 등에 활용할 수 있습니다.",
      },
    },
  },
  render: (args: any) => ({
    components: { ScKeypad },
    setup() {
      const inputValue = ref("");
      const maskedValue = ref("");

      const handleNumberClick = (value: string) => {
        if (inputValue.value.length < args.maxLength) {
          inputValue.value += value;
          maskedValue.value += "●";
        }
      };

      const handleDeleteClick = () => {
        if (inputValue.value.length > 0) {
          inputValue.value = inputValue.value.slice(0, -1);
          maskedValue.value = maskedValue.value.slice(0, -1);
        }
      };

      return { args, inputValue, maskedValue, handleNumberClick, handleDeleteClick };
    },
    template: `
      <div style="max-width: 400px;">
        <div style="margin-bottom: 20px; padding: 16px; background: #f5f5f5; border-radius: 8px;">
          <div style="font-size: 14px; font-weight: 600; margin-bottom: 8px;">비밀번호</div>
          <div style="font-size: 32px; letter-spacing: 8px; min-height: 40px;">
            {{ maskedValue || '&nbsp;' }}
          </div>
          <div style="font-size: 12px; color: #666; margin-top: 4px;">
            {{ inputValue.length }} / {{ args.maxLength }} 자리
          </div>
        </div>
        
        <ScKeypad 
          v-bind="args"
          @number-click="handleNumberClick"
          @delete-click="handleDeleteClick"
        />
      </div>
    `,
  }),
};

export const Interactive: Story = {
  args: {
    isDarkTheme: false,
    showRearrange: false,
    maxLength: 16,
  },
  parameters: {
    docs: {
      description: {
        story:
          "테마 전환과 재배열 기능을 테스트할 수 있는 인터랙티브 예시입니다. 버튼을 클릭하여 키패드의 다양한 기능을 체험해보세요.",
      },
    },
  },
  render: (args: any) => ({
    components: { ScKeypad },
    setup() {
      const isDark = ref(false);
      const showRearrange = ref(false);
      const inputValue = ref("");

      const toggleTheme = () => {
        isDark.value = !isDark.value;
      };

      const toggleRearrange = () => {
        showRearrange.value = !showRearrange.value;
      };

      const handleNumberClick = (value: string) => {
        inputValue.value += value;
      };

      const handleDeleteClick = () => {
        if (inputValue.value.length > 0) {
          inputValue.value = inputValue.value.slice(0, -1);
        }
      };

      const resetInput = () => {
        inputValue.value = "";
      };

      return {
        isDark,
        showRearrange,
        inputValue,
        toggleTheme,
        toggleRearrange,
        handleNumberClick,
        handleDeleteClick,
        resetInput,
      };
    },
    template: `
      <div style="max-width: 400px;">
        <div style="margin-bottom: 20px; padding: 16px; background: #f5f5f5; border-radius: 8px;">
          <div style="display: flex; gap: 8px; margin-bottom: 12px;">
            <button 
              @click="toggleTheme"
              style="flex: 1; padding: 8px; border: 1px solid #ddd; border-radius: 4px; background: white; cursor: pointer;"
            >
              {{ isDark ? '🌞 라이트' : '🌙 다크' }} 테마
            </button>
            <button 
              @click="toggleRearrange"
              style="flex: 1; padding: 8px; border: 1px solid #ddd; border-radius: 4px; background: white; cursor: pointer;"
            >
              {{ showRearrange ? '⬜ 00' : '🔄 재배열' }} 버튼
            </button>
            <button 
              @click="resetInput"
              style="flex: 1; padding: 8px; border: 1px solid #ddd; border-radius: 4px; background: white; cursor: pointer;"
            >
              🔄 초기화
            </button>
          </div>
          
          <div style="font-size: 14px; font-weight: 600; margin-bottom: 8px;">입력값</div>
          <div style="font-size: 24px; letter-spacing: 2px; font-family: monospace; min-height: 32px;">
            {{ inputValue || '&nbsp;' }}
          </div>
          <div style="font-size: 12px; color: #666; margin-top: 4px;">
            {{ inputValue.length }} / 16 자리
          </div>
        </div>
        
        <ScKeypad 
          :isDarkTheme="isDark"
          :showRearrange="showRearrange"
          :maxLength="16"
          @number-click="handleNumberClick"
          @delete-click="handleDeleteClick"
        />
      </div>
    `,
  }),
};

