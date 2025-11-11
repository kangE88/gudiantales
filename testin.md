 <!-- 스케줄 모드일 때는 ScheduleDayButton 사용 -->
                <ScheduleDayButton
                  v-else-if="isScheduleMode && !isWeeklyView"
                  :day="day"
                  :schedules="day.schedules || []"
                  :class="[getDayButtonClass(day), getAttributesClass(day)]"
                  :style="getAttributeStyle(day)"
                  :aria-label="getDayButtonLabel(day)"
                  @click="selectDate"
                >
                  <template v-if="$slots['day-content']" #day-content="slotProps">
                    <slot name="day-content" v-bind="slotProps" />
                  </template>
                </ScheduleDayButton>


                >>>

                <template>
  <DatePicker v-bind="{...$attrs, ...mappingProps}">
    <template v-if="$slots.header" #header>
      <slot name="header" />
    </template>
    <template v-if="$slots.options" #options>
      <slot name="options" />
    </template>
    <template v-if="$slots['day-content']" #day-content="slotProps">
      <slot name="day-content" v-bind="slotProps" />
    </template>
  </DatePicker>
</template>

<script lang="ts">
import {type DatePickerProps} from "@/index.ts"

export type ScheduleDatePickerProps = Omit<DatePickerProps, "mode" | "type">
</script>

<script setup lang="ts">
import {DatePicker} from "@/index.ts"
import {computed} from "vue"

const props = withDefaults(defineProps<ScheduleDatePickerProps>(), {})

const mappingProps = computed<DatePickerProps>(() => ({
  ...props,
  mode: "single",
  type: "schedule"
}))

defineOptions({
  inheritAttrs: false
})
</script>
