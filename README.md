import {
  BasicCard,
  BasicChipGroup,
  BottomSheet,
  BoxButton,
  Divider,
  Icon,
  ScheduleDatePicker,
  SegmentSwitch,
  TextDropdown,
  Tooltip,
  WheelPicker,
} from "@shc-nss/ui/solid";
import { fn } from "@storybook/test";
import type { Meta, StoryObj } from "@storybook/vue3";
import { addDays, format, isSameDay, subDays } from "date-fns";
import { computed, ref } from "vue";

const meta: Meta<typeof ScheduleDatePicker> = {
  title: "Solid-Modules/DatePicker/ScheduleDatePicker",
  component: ScheduleDatePicker,
  args: {
    defaultView: "monthly",
    onClickDay: fn(),
    onClickHeader: fn(),
    "onUpdate:modelValue": fn(),
    "onUpdate:viewDate": fn(),
    data: [
      { id: 1, type: "label", labelColor: "blue", date: new Date(), title: "테스트테스트" },
      { id: 2, type: "income", date: new Date(), title: "+1111111" },
      { id: 3, type: "expense", date: new Date(), title: "-1234234" },
      { id: 4, type: "label", labelColor: "green", date: new Date(), title: "테스트테스트" },
      { id: 5, type: "label", labelColor: "blue", date: new Date(), title: "테스트테스트" },
    ],
  },
  argTypes: {
    minDate: {
      control: "date",
      description: "최소 날짜",
      table: {
        category: "props",
        type: { summary: "Date" },
        defaultValue: { summary: "() => new Date(1900, 1, 1)" },
      },
    },
    maxDate: {
      control: "date",
      description: "최대 날짜",
      table: {
        category: "props",
        type: { summary: "Date" },
        defaultValue: { summary: "() => new Date(2100, 12, 31)" },
      },
    },
    showOutsideDays: {
      control: "boolean",
      description: "해당 달이 아닌 날짜 노출 여부",
      table: {
        category: "props",
        type: { summary: "boolean" },
        defaultValue: { summary: "false" },
      },
    },
    fixedWeeks: {
      control: "boolean",
      description: "달력 높이 고정 여부 (6주)",
      table: {
        category: "props",
        type: { summary: "boolean" },
        defaultValue: { summary: "false" },
      },
    },
    isDateDisabled: {
      control: "object",
      description: "날짜 비활성화 체크 함수",
      table: {
        category: "props",
        type: { summary: "(date: Date) => boolean" },
      },
    },
    isDateHoliday: {
      control: "object",
      description: "날짜 공휴일 체크 함수",
      table: {
        category: "props",
        type: { summary: "(date: Date) => boolean" },
      },
    },
    formatters: {
      control: "object",
      description: "헤더, 요일 포맷 함수",
      table: {
        category: "props",
        type: { summary: "(date: Date) => boolean" },
      },
    },
    highlightWeekends: {
      control: "boolean",
      description: "주말, 공휴일 색깔 강조 여부",
      table: {
        category: "props",
        type: { summary: "boolean" },
        defaultValue: { summary: "false" },
      },
    },
    defaultView: {
      control: "radio",
      options: ["weekly", "monthly"],
      description: "주간/월간 달력",
      table: {
        category: "props",
        type: { summary: `"weekly" | "monthly"` },
        defaultValue: { summary: "monthly" },
      },
    },
    showToggleView: {
      control: "boolean",
      description: "주/월 토글 여부",
      table: {
        category: "props",
        type: { summary: "boolean" },
        defaultValue: { summary: "false" },
      },
    },
    disabled: {
      control: "boolean",
      description: "비활성화 여부",
      table: {
        category: "props",
        type: { summary: "boolean" },
        defaultValue: { summary: "false" },
      },
    },
    attributes: {
      control: "object",
      description: "키: 날짜 문자열 (YYYY-MM-DD), 값: 날짜에 적용할 attributes",
      table: {
        category: "props",
        type: { summary: "Record<string, DatePickerAttributeValue>" },
        detail: `- dot?: boolean | string\n- highlight?: boolean | string\n- class?: string\n- ariaLabel?: string`,
      },
    },
    modelValue: {
      control: false,
      description: "선택된 값",
      table: {
        category: "model",
        type: { summary: "Date | {from: Date | null; to?: Date | null}" },
        defaultValue: { summary: "null" },
      },
    },
    viewDate: {
      control: "date",
      description: "보고 있는 날짜",
      table: {
        category: "model",
        type: { summary: "Date" },
        defaultValue: { summary: "() => new Date()" },
      },
    },
    data: {
      control: "object",
      description: "스케줄 데이터",
      table: {
        category: "props",
        type: {
          summary: "DatePickerScheduleItem[]",
          detail: `- id: string | number\n- date: Date\n- title: string\n- type: 'income' | 'expense' | 'label'\n- labelColor?: TintLabelProps["color"]`,
        },
        defaultValue: { summary: "() => []" },
      },
    },
    "update:modelValue": {
      table: {
        category: "events",
        type: { summary: "Date | {from: Date | null; to?: Date | null}" },
      },
    },
    "update:viewDate": {
      table: {
        category: "events",
        type: { summary: "Date" },
      },
    },
    clickDay: {
      description: "날짜 클릭 시",
      table: {
        category: "events",
        type: { summary: "Date" },
      },
    },
    clickHeader: {
      description: "연월 헤더 클릭 시",
      table: {
        category: "events",
        type: { summary: "Date" },
      },
    },
    onClickHeader: {
      table: { disable: true },
    },
    onClickDay: {
      table: { disable: true },
    },
    "onUpdate:viewDate": {
      table: { disable: true },
    },
    "onUpdate:modelValue": {
      table: { disable: true },
    },
    "day-content": {
      table: {
        type: {
          summary: "CalendarDay",
          detail: `- date: Date\n- disabled: boolean\n- selected: boolean\n- today: boolean\n- holiday: boolean\n- otherMonth: boolean\n- schedules: DatePickerScheduleItem[]`,
        },
      },
    },
  },
};

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  render: (args) => ({
    components: { ScheduleDatePicker, Icon },
    setup() {
      const selectedDate = ref(null);

      return {
        args,
        selectedDate,
      };
    },
    template: `
			<div style="width: 360px;max-width:100%;">
				<ScheduleDatePicker 
					v-bind="args" 
					v-model="selectedDate"
				/>
				<p style="margin-top: 20px; text-align: center;">Selected: {{ selectedDate || 'None' }}</p>
			</div>
		`,
  }),
};

export const Data: Story = {
  parameters: {
    docs: {
      description: {
        story: `
- \`{
    id: string | number; 
    date: Date; 
    title: string; 
    type: 'income' | 'expense' | 'label'; 
    labelColor?: TintLabelProps["color"];
}\`
- \`type\`을 \`label\`으로 설정하면 \`TintLabel\` 컴포넌트가 노출되고 \`labelColor\`를 통해 색깔을 설정할 수 있습니다.
- \`type\`을 \`income\`으로 설정하면 파란색으로 텍스트가 노출됩니다.
- \`type\`을 \`expense\`으로 설정하면 빨간색으로 텍스트가 노출됩니다.
- 데이터는 최대 3건까지만 노출되고, 나머지는 건수로 노출됩니다.
`,
      },
    },
  },
  render: () => ({
    components: { ScheduleDatePicker },
    setup() {
      const today = new Date();
      const date1 = subDays(today, 1);
      const date2 = addDays(today, 1);
      const data = [
        { id: 1, type: "label", labelColor: "blue", date: today, title: "label" },
        { id: 2, type: "income", date: today, title: "income" },
        { id: 3, type: "expense", date: today, title: "expense" },
        { id: 4, type: "label", labelColor: "green", date: today, title: "텍스트" },
        { id: 5, type: "label", labelColor: "blue", date: today, title: "텍스트" },
        { id: 6, type: "label", labelColor: "yellow", date: date1, title: "텍스트" },
        { id: 7, type: "label", labelColor: "cyan", date: date2, title: "긴텍스트긴텍스트" },
        { id: 8, type: "label", labelColor: "yellow", date: date2, title: "텍스트" },
      ];

      return {
        data,
      };
    },
    template: `
			<div style="width: 360px;max-width:100%;">
				<ScheduleDatePicker :data="data" />
			</div>
		`,
  }),
};

export const FontResize: Story = {
  parameters: {
    docs: {
      description: {
        story: `
- 폰트 사이즈를 container 너비에 맞추어 reszing 처리합니다.
`,
      },
    },
  },
  args: {
    data: [
      { id: 1, type: "income", date: new Date(), title: "+100" },
      { id: 2, type: "income", date: new Date(), title: "+1234567890" },
      { id: 3, type: "income", date: new Date(), title: "+123456790123456" },
    ],
  },
  render: (args) => ({
    components: { ScheduleDatePicker },
    setup() {
      return {
        args,
      };
    },
    template: `
			<div style="width: 360px;max-width:100%;">
				<ScheduleDatePicker v-bind="args" />
			</div>
		`,
  }),
};

export const WithOptionsSlot: Story = {
  parameters: {
    docs: {
      description: {
        story: `
- \`options\` slot을 통해 타이틀과 달력 사이에 내용을 추가할 수 있습니다.
`,
      },
    },
  },
  render: () => ({
    components: { ScheduleDatePicker, BasicChipGroup },
    setup() {
      const chipItems = [
        { text: "전체선택", value: "1" },
        { text: "텍스트1", value: "2" },
        { text: "텍스트2", value: "3" },
      ];

      return {
        chipItems,
      };
    },
    template: `
			<div style="width: 360px;max-width:100%;">
				<ScheduleDatePicker>
          <template #options>
            <BasicChipGroup :items="chipItems" variant="outline" />
          </template>
        </ScheduleDatePicker>
			</div>
		`,
  }),
};

export const WithHeaderSlot: Story = {
  render: () => ({
    components: {
      ScheduleDatePicker,
      BottomSheet,
      WheelPicker,
      BoxButton,
      TextDropdown,
      Tooltip,
      SegmentSwitch,
    },
    setup() {
      const modelValue = ref(new Date("2025-08-01"));
      const isOpen = ref(false);
      const pickerValue = ref([]);
      const options = [
        Array.from({ length: 7 }, (_, i) => ({ label: i + 2020, value: i + 2020 })),
        Array.from({ length: 12 }, (_, i) => ({ label: i + 1, value: i + 1 })),
      ];

      function onClick() {
        //@ts-ignore
        pickerValue.value = [modelValue.value.getFullYear(), modelValue.value.getMonth() + 1];
        isOpen.value = true;
      }

      function changeViewDate() {
        modelValue.value = new Date(pickerValue.value.join("-"));
        isOpen.value = false;
      }

      const formattedViewDate = computed(() => format(modelValue.value, "yyyy년 MM월"));

      return {
        modelValue,
        isOpen,
        pickerValue,
        options,
        onClick,
        changeViewDate,
        formattedViewDate,
      };
    },
    template: `
			<div style="width: 360px;max-width:100%;">
				<ScheduleDatePicker v-model:viewDate="modelValue">
          <template #header>
            <div style="width: 100%;">
              <div style="display: flex; justify-content: space-between; align-items: center;">
                <TextDropdown
                  size="large"
                  aria-label="달력 연월 선택"
                  :value="formattedViewDate"
                  @click="onClick"
                />
                <SegmentSwitch :items="[{iconName: 'calender'},{iconName: 'Menu'}]" size='xsmall' />
              </div>
              <div style="display: flex; justify-content: space-between; align-items: center; margin: 1rem 0;">
                <div>
                  <span style="color: gray; margin-right: 1rem;">지출</span><span>10,459,000,원</span>
                </div>
                <Tooltip content="툴팁입니다." />
              </div>
            </div>
          </template>
        </ScheduleDatePicker>

        <BottomSheet v-model="isOpen">
          <WheelPicker
            :options="options"
            v-model="pickerValue"
            :aria-labels="['연도 선택', '월 선택', '일 선택']"
          />
          <template #footer>
            <BoxButton @click="changeViewDate" text="선택" />
          </template>
        </BottomSheet>
			</div>
		`,
  }),
};

export const WithDayContentSlot: Story = {
  render: () => ({
    components: {
      ScheduleDatePicker,
      BottomSheet,
      WheelPicker,
      BoxButton,
      Icon,
      BasicCard,
      Divider,
    },
    setup() {
      const modelValue = ref(new Date("2025-08-01"));
      const isOpen = ref(false);
      const pickerValue = ref([]);
      const options = [
        Array.from({ length: 7 }, (_, i) => ({ label: i + 2020, value: i + 2020 })),
        Array.from({ length: 12 }, (_, i) => ({ label: i + 1, value: i + 1 })),
      ];

      function onClick() {
        //@ts-ignore
        pickerValue.value = [modelValue.value.getFullYear(), modelValue.value.getMonth() + 1];
        isOpen.value = true;
      }

      function changeViewDate() {
        modelValue.value = new Date(pickerValue.value.join("-"));
        isOpen.value = false;
      }

      const items = [
        { date: new Date("2025-08-01"), icon: "heart" },
        { date: new Date("2025-08-02"), icon: "alert" },
        { date: new Date("2025-08-10"), icon: "edit" },
      ];

      function getIcon(targetDate: Date) {
        return items.find(({ date }) => isSameDay(date, targetDate))?.icon;
      }

      return {
        modelValue,
        isOpen,
        pickerValue,
        options,
        items,
        onClick,
        changeViewDate,
        getIcon,
      };
    },
    template: `
			<div style="width: 355px;max-width:100%;">
				<ScheduleDatePicker v-model:viewDate="modelValue" @clickHeader="onClick">
          <template #options>
            <BasicCard style="margin: 1rem 0;">
              <div style="display: flex; align-items: center; justify-content: center; color: gray;">
                <div style="flex: 1; text-align: center;"><span>기록횟수</span><strong style="margin-left: 0.5rem">5회</strong></div>
                <Divider orientation="vertical" style="height: 15px" />
                <div style="flex: 1; text-align: center;"><span>평균점수</span><strong style="margin-left: 0.5rem">70점</strong></div>
              </div>
            </BasicCard>
          </template>
          <template #day-content="{day}">
            <div v-if="getIcon(day.date)" style="display: flex; width: 100%; justify-content: center;">
              <Icon :name="getIcon(day.date)" />
            </div>
          </template>
        </ScheduleDatePicker>

        <BottomSheet v-model="isOpen">
          <WheelPicker
            :options="options"
            v-model="pickerValue"
            :aria-labels="['연도 선택', '월 선택', '일 선택']"
          />
          <template #footer>
            <BoxButton @click="changeViewDate" text="선택" />
          </template>
        </BottomSheet>
			</div>
		`,
  }),
};

export const Attributes: Story = {
  render: (args) => ({
    components: { ScheduleDatePicker },
    setup() {
      const today = new Date();

      const samples = [
        {
          offset: 1,
          attribute: {
            highlight: true,
            ariaLabel: "기본 highlight",
          },
        },
        {
          offset: 2,
          attribute: {
            dot: true,
            ariaLabel: "기본 dot",
          },
        },
        {
          offset: 3,
          attribute: {
            dot: "#16a34a",
            highlight: "#dcfce7",
            ariaLabel: "hex 컬러",
          },
        },
        {
          offset: 4,
          attribute: {
            highlight: true,
            dot: true,
            class: "story-datepicker-attribute--warning",
            ariaLabel: "custom class",
          },
        },
        {
          offset: 5,
          attribute: {
            highlight: "var(--brand-100)",
            dot: "var(--bg-red-same)",
            ariaLabel: "CSS 변수",
          },
        },
      ].map((sample) => {
        const date = addDays(today, sample.offset);
        return {
          ...sample,
          date,
          key: format(date, "yyyy-MM-dd"),
        };
      });

      const attributes = ref<Record<string, any>>(
        samples.reduce((acc, cur) => ({ ...acc, [cur.key]: cur.attribute }), {})
      );

      const viewDate = ref(today);

      return { args, attributes, samples, viewDate };
    },
    template: `
			<div style="width: 360px;max-width:100%;">
        <ScheduleDatePicker
          v-bind="args"
          :attributes="attributes"
        />
        <div class="story-datepicker-attributes-legend">
          <ul>
            <li v-for="sample in samples" :key="sample.key">
              <strong>{{ sample.key }}</strong> {{ sample.attribute }}
            </li>
          </ul>
        </div>
      </div>
    `,
  }),
};
