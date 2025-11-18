import type { Meta, StoryObj } from '@storybook/vue3'
import { ref } from 'vue'
import { useSortableCustom, useSortableList } from './useSortable'

const meta: Meta<typeof useSortableList> = {
  title: 'Composables/useSortable',
  parameters: {
    docs: {
      description: {
        component: '드래그 앤 드롭으로 리스트 정렬을 가능하게 하는 composable입니다. Sortable.js 기반으로 다양한 옵션을 제공합니다.',
      },
    },
  },
  tags: ['autodocs'],
}

export default meta
type Story = StoryObj<typeof useSortableList>

/**
 * 가장 기본적인 사용 예제입니다.
 * 
 * - 기본 옵션이 자동으로 적용됩니다
 * - animation: 150ms
 * - 자동 스크롤 활성화
 * - 항목을 드래그하여 순서를 변경해보세요!
 */
export const Default: Story = {
  render: () => ({
    setup() {
      const listRef = ref<HTMLElement>()
      const items = ref([
        { id: 1, name: '🍎 사과', color: '#ff6b6b' },
        { id: 2, name: '🍊 오렌지', color: '#ffa94d' },
        { id: 3, name: '🍋 레몬', color: '#ffd43b' },
        { id: 4, name: '🍇 포도', color: '#a78bfa' },
        { id: 5, name: '🍓 딸기', color: '#f472b6' },
      ])

      useSortableList(listRef, items)

      return { listRef, items }
    },
    template: `
      <div style="padding: 20px; max-width: 600px; margin: 0 auto;">
        <div style="margin-bottom: 20px; padding: 15px; background: #f0f7ff; border-radius: 8px; border-left: 4px solid #0066cc;">
          <p style="margin: 0; color: #0066cc; font-weight: 600;">💡 Tip</p>
          <p style="margin: 5px 0 0 0; color: #333;">항목을 드래그하여 순서를 변경해보세요!</p>
        </div>

        <ul ref="listRef" style="list-style: none; padding: 0; margin: 0;">
          <li 
            v-for="item in items" 
            :key="item.id"
            style="padding: 15px 20px; margin-bottom: 10px; background: white; border-radius: 8px; cursor: move; box-shadow: 0 2px 4px rgba(0,0,0,0.1); transition: transform 0.2s, box-shadow 0.2s; user-select: none;"
            @mouseenter="$event.currentTarget.style.transform = 'translateY(-2px)'; $event.currentTarget.style.boxShadow = '0 4px 8px rgba(0,0,0,0.15)'"
            @mouseleave="$event.currentTarget.style.transform = 'translateY(0)'; $event.currentTarget.style.boxShadow = '0 2px 4px rgba(0,0,0,0.1)'"
          >
            <div style="display: flex; align-items: center; gap: 12px;">
              <span style="font-size: 20px;">⋮⋮</span>
              <span style="font-size: 18px; font-weight: 600;">{{ item.name }}</span>
            </div>
          </li>
        </ul>

        <div style="margin-top: 20px; padding: 15px; background: #f8f9fa; border-radius: 8px;">
          <p style="margin: 0; font-size: 14px; color: #666;">현재 순서: {{ items.map(i => i.name).join(' → ') }}</p>
        </div>
      </div>
    `,
  }),
}

/**
 * Handle 옵션을 사용한 예제입니다.
 * 
 * - 특정 요소(핸들)를 잡아야만 드래그 가능
 * - 실수로 인한 드래그 방지
 * - 항목 내 다른 인터랙션과 공존 가능
 */
export const WithHandle: Story = {
  render: () => ({
    setup() {
      const listRef = ref<HTMLElement>()
      const tasks = ref([
        { id: 1, title: '프로젝트 기획', done: false },
        { id: 2, title: '디자인 작업', done: false },
        { id: 3, title: '개발 진행', done: true },
        { id: 4, title: '테스트', done: false },
        { id: 5, title: '배포', done: false },
      ])

      useSortableList(listRef, tasks, {
        handle: '.drag-handle',
      })

      const toggleTask = (task: any) => {
        task.done = !task.done
      }

      return { listRef, tasks, toggleTask }
    },
    template: `
      <div style="padding: 20px; max-width: 600px; margin: 0 auto;">
        <div style="margin-bottom: 20px; padding: 15px; background: #fff3cd; border-radius: 8px; border-left: 4px solid #ffc107;">
          <p style="margin: 0; color: #856404; font-weight: 600;">⚙️ Handle 모드</p>
          <p style="margin: 5px 0 0 0; color: #856404;">왼쪽의 ⋮⋮ 핸들을 잡아야만 드래그할 수 있습니다.</p>
        </div>

        <ul ref="listRef" style="list-style: none; padding: 0; margin: 0;">
          <li 
            v-for="task in tasks" 
            :key="task.id"
            style="padding: 15px; margin-bottom: 10px; background: white; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1);"
          >
            <div style="display: flex; align-items: center; gap: 12px;">
              <span 
                class="drag-handle" 
                style="font-size: 20px; cursor: move; color: #999; padding: 5px;"
                @mouseenter="$event.currentTarget.style.color = '#333'"
                @mouseleave="$event.currentTarget.style.color = '#999'"
              >
                ⋮⋮
              </span>
              <input 
                type="checkbox" 
                :checked="task.done"
                @change="toggleTask(task)"
                style="width: 18px; height: 18px; cursor: pointer;"
              />
              <span 
                :style="{ 
                  flex: 1, 
                  fontSize: '16px',
                  textDecoration: task.done ? 'line-through' : 'none',
                  color: task.done ? '#999' : '#333'
                }"
              >
                {{ task.title }}
              </span>
              <span v-if="task.done" style="background: #10b981; color: white; padding: 4px 8px; border-radius: 4px; font-size: 12px;">
                완료
              </span>
            </div>
          </li>
        </ul>
      </div>
    `,
  }),
}

/**
 * Filter 옵션을 사용한 예제입니다.
 * 
 * - 특정 항목의 드래그를 방지
 * - locked 상태의 항목은 이동 불가
 * - 동적으로 잠금/해제 가능
 */
export const WithFilter: Story = {
  render: () => ({
    setup() {
      const listRef = ref<HTMLElement>()
      const items = ref([
        { id: 1, name: '헤더', locked: true },
        { id: 2, name: '네비게이션', locked: false },
        { id: 3, name: '메인 컨텐츠', locked: false },
        { id: 4, name: '사이드바', locked: false },
        { id: 5, name: '푸터', locked: true },
      ])

      useSortableList(listRef, items, {
        filter: '.locked-item',
        onFilter: (evt) => {
          console.log('잠긴 항목 클릭:', evt)
        },
      })

      const toggleLock = (item: any) => {
        item.locked = !item.locked
      }

      return { listRef, items, toggleLock }
    },
    template: `
      <div style="padding: 20px; max-width: 600px; margin: 0 auto;">
        <div style="margin-bottom: 20px; padding: 15px; background: #fef3c7; border-radius: 8px; border-left: 4px solid #f59e0b;">
          <p style="margin: 0; color: #92400e; font-weight: 600;">🔒 Filter 모드</p>
          <p style="margin: 5px 0 0 0; color: #92400e;">잠긴 항목은 드래그할 수 없습니다.</p>
        </div>

        <ul ref="listRef" style="list-style: none; padding: 0; margin: 0;">
          <li 
            v-for="item in items" 
            :key="item.id"
            :class="{ 'locked-item': item.locked }"
            :style="{
              padding: '15px 20px',
              marginBottom: '10px',
              background: item.locked ? '#f3f4f6' : 'white',
              borderRadius: '8px',
              boxShadow: '0 2px 4px rgba(0,0,0,0.1)',
              cursor: item.locked ? 'not-allowed' : 'move',
              opacity: item.locked ? 0.6 : 1,
            }"
          >
            <div style="display: flex; align-items: center; justify-content: space-between;">
              <div style="display: flex; align-items: center; gap: 12px;">
                <span style="font-size: 20px;">{{ item.locked ? '🔒' : '⋮⋮' }}</span>
                <span style="font-size: 16px; font-weight: 500;">{{ item.name }}</span>
              </div>
              <button
                @click="toggleLock(item)"
                :style="{
                  padding: '6px 12px',
                  fontSize: '14px',
                  borderRadius: '6px',
                  border: 'none',
                  cursor: 'pointer',
                  fontWeight: '500',
                  background: item.locked ? '#e5e7eb' : '#3b82f6',
                  color: item.locked ? '#374151' : 'white',
                }"
              >
                {{ item.locked ? '잠금 해제' : '잠금' }}
              </button>
            </div>
          </li>
        </ul>
      </div>
    `,
  }),
}

/**
 * 다중 리스트 간 항목 이동 예제입니다.
 * 
 * - Group 옵션으로 여러 리스트 연결
 * - 리스트 간 자유로운 항목 이동
 * - 할 일 → 진행 중 → 완료 워크플로우
 */
export const MultipleListsKanban: Story = {
  render: () => ({
    setup() {
      const todoRef = ref<HTMLElement>()
      const inProgressRef = ref<HTMLElement>()
      const doneRef = ref<HTMLElement>()

      const todoItems = ref([
        { id: 1, title: 'UI 디자인', priority: 'high' },
        { id: 2, title: 'API 문서 작성', priority: 'medium' },
        { id: 3, title: '테스트 코드', priority: 'low' },
      ])

      const inProgressItems = ref([
        { id: 4, title: '로그인 기능', priority: 'high' },
      ])

      const doneItems = ref([
        { id: 5, title: '프로젝트 세팅', priority: 'medium' },
      ])

      useSortableList(todoRef, todoItems, {
        group: 'kanban',
        animation: 200,
      })

      useSortableList(inProgressRef, inProgressItems, {
        group: 'kanban',
        animation: 200,
      })

      useSortableList(doneRef, doneItems, {
        group: 'kanban',
        animation: 200,
      })

      const getPriorityColor = (priority: string) => {
        switch (priority) {
          case 'high': return '#ef4444'
          case 'medium': return '#f59e0b'
          case 'low': return '#10b981'
          default: return '#6b7280'
        }
      }

      return { todoRef, inProgressRef, doneRef, todoItems, inProgressItems, doneItems, getPriorityColor }
    },
    template: `
      <div style="padding: 20px;">
        <div style="margin-bottom: 20px; padding: 15px; background: #e7f3ff; border-radius: 8px; border-left: 4px solid #0066cc;">
          <p style="margin: 0; color: #0066cc; font-weight: 600;">📋 칸반 보드</p>
          <p style="margin: 5px 0 0 0; color: #333;">리스트 간 자유롭게 항목을 이동할 수 있습니다.</p>
        </div>

        <div style="display: grid; grid-template-columns: repeat(3, 1fr); gap: 20px; max-width: 1200px;">
          <!-- 할 일 -->
          <div style="background: #f8f9fa; border-radius: 12px; padding: 15px;">
            <div style="display: flex; align-items: center; gap: 8px; margin-bottom: 15px;">
              <h3 style="margin: 0; font-size: 18px;">📝 할 일</h3>
              <span style="background: #e5e7eb; color: #4b5563; padding: 2px 8px; border-radius: 12px; font-size: 12px; font-weight: 600;">
                {{ todoItems.length }}
              </span>
            </div>
            <ul ref="todoRef" style="list-style: none; padding: 0; margin: 0; min-height: 200px;">
              <li 
                v-for="item in todoItems" 
                :key="item.id"
                style="padding: 12px; margin-bottom: 8px; background: white; border-radius: 8px; cursor: move; box-shadow: 0 1px 3px rgba(0,0,0,0.1);"
              >
                <div style="font-weight: 500; margin-bottom: 6px;">{{ item.title }}</div>
                <span 
                  :style="{
                    display: 'inline-block',
                    padding: '2px 8px',
                    borderRadius: '4px',
                    fontSize: '12px',
                    color: 'white',
                    background: getPriorityColor(item.priority)
                  }"
                >
                  {{ item.priority }}
                </span>
              </li>
            </ul>
          </div>

          <!-- 진행 중 -->
          <div style="background: #fffbeb; border-radius: 12px; padding: 15px;">
            <div style="display: flex; align-items: center; gap: 8px; margin-bottom: 15px;">
              <h3 style="margin: 0; font-size: 18px;">🚀 진행 중</h3>
              <span style="background: #fef3c7; color: #92400e; padding: 2px 8px; border-radius: 12px; font-size: 12px; font-weight: 600;">
                {{ inProgressItems.length }}
              </span>
            </div>
            <ul ref="inProgressRef" style="list-style: none; padding: 0; margin: 0; min-height: 200px;">
              <li 
                v-for="item in inProgressItems" 
                :key="item.id"
                style="padding: 12px; margin-bottom: 8px; background: white; border-radius: 8px; cursor: move; box-shadow: 0 1px 3px rgba(0,0,0,0.1);"
              >
                <div style="font-weight: 500; margin-bottom: 6px;">{{ item.title }}</div>
                <span 
                  :style="{
                    display: 'inline-block',
                    padding: '2px 8px',
                    borderRadius: '4px',
                    fontSize: '12px',
                    color: 'white',
                    background: getPriorityColor(item.priority)
                  }"
                >
                  {{ item.priority }}
                </span>
              </li>
            </ul>
          </div>

          <!-- 완료 -->
          <div style="background: #f0fdf4; border-radius: 12px; padding: 15px;">
            <div style="display: flex; align-items: center; gap: 8px; margin-bottom: 15px;">
              <h3 style="margin: 0; font-size: 18px;">✅ 완료</h3>
              <span style="background: #d1fae5; color: #065f46; padding: 2px 8px; border-radius: 12px; font-size: 12px; font-weight: 600;">
                {{ doneItems.length }}
              </span>
            </div>
            <ul ref="doneRef" style="list-style: none; padding: 0; margin: 0; min-height: 200px;">
              <li 
                v-for="item in doneItems" 
                :key="item.id"
                style="padding: 12px; margin-bottom: 8px; background: white; border-radius: 8px; cursor: move; box-shadow: 0 1px 3px rgba(0,0,0,0.1); opacity: 0.8;"
              >
                <div style="font-weight: 500; margin-bottom: 6px; text-decoration: line-through;">{{ item.title }}</div>
                <span 
                  :style="{
                    display: 'inline-block',
                    padding: '2px 8px',
                    borderRadius: '4px',
                    fontSize: '12px',
                    color: 'white',
                    background: getPriorityColor(item.priority)
                  }"
                >
                  {{ item.priority }}
                </span>
              </li>
            </ul>
          </div>
        </div>
      </div>
    `,
  }),
}

/**
 * 가로 방향 정렬 예제입니다.
 * 
 * - direction: 'horizontal' 옵션
 * - 카드 형태의 가로 정렬
 * - flex 레이아웃과 함께 사용
 */
export const HorizontalDirection: Story = {
  render: () => ({
    setup() {
      const listRef = ref<HTMLElement>()
      const cards = ref([
        { id: 1, title: '월요일', emoji: '🌙', color: '#dbeafe' },
        { id: 2, title: '화요일', emoji: '🔥', color: '#fed7aa' },
        { id: 3, title: '수요일', emoji: '💧', color: '#bfdbfe' },
        { id: 4, title: '목요일', emoji: '🌳', color: '#bbf7d0' },
        { id: 5, title: '금요일', emoji: '⭐', color: '#fef08a' },
      ])

      useSortableList(listRef, cards, {
        direction: 'horizontal',
        animation: 200,
      })

      return { listRef, cards }
    },
    template: `
      <div style="padding: 20px;">
        <div style="margin-bottom: 20px; padding: 15px; background: #f0fff4; border-radius: 8px; border-left: 4px solid #10b981;">
          <p style="margin: 0; color: #065f46; font-weight: 600;">↔️ 가로 정렬</p>
          <p style="margin: 5px 0 0 0; color: #065f46;">카드를 좌우로 드래그하여 순서를 변경하세요.</p>
        </div>

        <div 
          ref="listRef" 
          style="display: flex; gap: 15px; padding: 20px; background: #f8f9fa; border-radius: 12px; overflow-x: auto;"
        >
          <div 
            v-for="card in cards" 
            :key="card.id"
            :style="{
              minWidth: '180px',
              padding: '30px 20px',
              background: card.color,
              borderRadius: '12px',
              cursor: 'move',
              boxShadow: '0 4px 6px rgba(0,0,0,0.1)',
              transition: 'transform 0.2s, box-shadow 0.2s',
              textAlign: 'center',
            }"
            @mouseenter="$event.currentTarget.style.transform = 'translateY(-4px)'; $event.currentTarget.style.boxShadow = '0 8px 12px rgba(0,0,0,0.15)'"
            @mouseleave="$event.currentTarget.style.transform = 'translateY(0)'; $event.currentTarget.style.boxShadow = '0 4px 6px rgba(0,0,0,0.1)'"
          >
            <div style="font-size: 48px; margin-bottom: 10px;">{{ card.emoji }}</div>
            <div style="font-size: 18px; font-weight: 600; color: #1f2937;">{{ card.title }}</div>
          </div>
        </div>
      </div>
    `,
  }),
}

/**
 * 이벤트 핸들러 예제입니다.
 * 
 * - onStart, onEnd, onUpdate 등 이벤트 로깅
 * - 드래그 동작에 따른 피드백
 * - 실시간 상태 모니터링
 */
export const WithEventHandlers: Story = {
  render: () => ({
    setup() {
      const listRef = ref<HTMLElement>()
      const items = ref([
        { id: 1, name: '항목 A' },
        { id: 2, name: '항목 B' },
        { id: 3, name: '항목 C' },
        { id: 4, name: '항목 D' },
      ])

      const logs = ref<string[]>([])

      const addLog = (message: string) => {
        const timestamp = new Date().toLocaleTimeString()
        logs.value.unshift(`[${timestamp}] ${message}`)
        if (logs.value.length > 5) {
          logs.value = logs.value.slice(0, 5)
        }
      }

      useSortableList(listRef, items, {
        onStart: (evt) => {
          addLog(`🟢 드래그 시작: ${items.value[evt.oldIndex!].name}`)
        },
        onEnd: (evt) => {
          addLog(`🔴 드래그 종료: ${evt.oldIndex} → ${evt.newIndex}`)
        },
        onUpdate: (evt) => {
          addLog(`📝 순서 변경: ${evt.oldIndex} → ${evt.newIndex}`)
        },
      })

      return { listRef, items, logs }
    },
    template: `
      <div style="padding: 20px; max-width: 800px; margin: 0 auto;">
        <div style="margin-bottom: 20px; padding: 15px; background: #fef2f2; border-radius: 8px; border-left: 4px solid #ef4444;">
          <p style="margin: 0; color: #991b1b; font-weight: 600;">📊 이벤트 모니터링</p>
          <p style="margin: 5px 0 0 0; color: #991b1b;">드래그 동작이 실시간으로 로깅됩니다.</p>
        </div>

        <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 20px;">
          <!-- 리스트 -->
          <div>
            <h3 style="margin: 0 0 15px 0; font-size: 16px; color: #374151;">정렬 가능한 리스트</h3>
            <ul ref="listRef" style="list-style: none; padding: 0; margin: 0;">
              <li 
                v-for="item in items" 
                :key="item.id"
                style="padding: 15px 20px; margin-bottom: 10px; background: white; border-radius: 8px; cursor: move; box-shadow: 0 2px 4px rgba(0,0,0,0.1);"
              >
                <div style="display: flex; align-items: center; gap: 12px;">
                  <span style="font-size: 18px;">⋮⋮</span>
                  <span style="font-size: 16px; font-weight: 500;">{{ item.name }}</span>
                </div>
              </li>
            </ul>
          </div>

          <!-- 로그 -->
          <div>
            <h3 style="margin: 0 0 15px 0; font-size: 16px; color: #374151;">이벤트 로그</h3>
            <div style="background: #1f2937; border-radius: 8px; padding: 15px; min-height: 200px; font-family: monospace; font-size: 13px;">
              <div v-if="logs.length === 0" style="color: #9ca3af; font-style: italic;">
                항목을 드래그하면 로그가 표시됩니다...
              </div>
              <div v-for="(log, index) in logs" :key="index" style="color: #10b981; margin-bottom: 8px; line-height: 1.5;">
                {{ log }}
              </div>
            </div>
          </div>
        </div>

        <div style="margin-top: 20px; padding: 15px; background: #f8f9fa; border-radius: 8px;">
          <p style="margin: 0; font-size: 14px; color: #666;">
            <strong>현재 순서:</strong> {{ items.map(i => i.name).join(' → ') }}
          </p>
        </div>
      </div>
    `,
  }),
}

/**
 * Custom 옵션 사용 예제입니다.
 * 
 * - useSortableCustom으로 세밀한 제어
 * - 커스텀 클래스 지정
 * - 애니메이션 커스터마이징
 */
export const CustomOptions: Story = {
  render: () => ({
    setup() {
      const listRef = ref<HTMLElement>()
      const items = ref([
        { id: 1, name: '커스텀 항목 1', icon: '🎨' },
        { id: 2, name: '커스텀 항목 2', icon: '🎭' },
        { id: 3, name: '커스텀 항목 3', icon: '🎪' },
        { id: 4, name: '커스텀 항목 4', icon: '🎬' },
      ])

      useSortableCustom(listRef, items, {
        animation: 300,
        easing: 'cubic-bezier(0.25, 0.8, 0.25, 1)',
        ghostClass: 'sortable-ghost',
        dragClass: 'sortable-drag',
        chosenClass: 'sortable-chosen',
        delay: 100,
        delayOnTouchOnly: true,
      })

      return { listRef, items }
    },
    template: `
      <div style="padding: 20px; max-width: 600px; margin: 0 auto;">
        <div style="margin-bottom: 20px; padding: 15px; background: #f5f3ff; border-radius: 8px; border-left: 4px solid #8b5cf6;">
          <p style="margin: 0; color: #5b21b6; font-weight: 600;">🎨 커스텀 설정</p>
          <p style="margin: 5px 0 0 0; color: #5b21b6;">모든 옵션을 세밀하게 제어할 수 있습니다.</p>
        </div>

        <style>
          .sortable-ghost {
            opacity: 0.3;
            background: #e0e7ff !important;
          }
          .sortable-drag {
            opacity: 1;
            box-shadow: 0 8px 16px rgba(0,0,0,0.2) !important;
          }
          .sortable-chosen {
            background: #f5f3ff !important;
            border: 2px solid #8b5cf6 !important;
          }
        </style>

        <ul ref="listRef" style="list-style: none; padding: 0; margin: 0;">
          <li 
            v-for="item in items" 
            :key="item.id"
            style="padding: 20px; margin-bottom: 12px; background: white; border: 2px solid #e5e7eb; border-radius: 12px; cursor: move; transition: all 0.2s;"
          >
            <div style="display: flex; align-items: center; gap: 15px;">
              <span style="font-size: 32px;">{{ item.icon }}</span>
              <span style="font-size: 18px; font-weight: 600; color: #374151;">{{ item.name }}</span>
            </div>
          </li>
        </ul>

        <div style="margin-top: 20px; padding: 15px; background: #f8f9fa; border-radius: 8px;">
          <p style="margin: 0 0 8px 0; font-weight: 600; color: #374151;">적용된 옵션:</p>
          <ul style="margin: 0; padding-left: 20px; color: #6b7280; font-size: 14px;">
            <li>animation: 300ms</li>
            <li>easing: cubic-bezier (부드러운 이징)</li>
            <li>delay: 100ms (터치 전용)</li>
            <li>커스텀 클래스: ghost, drag, chosen</li>
          </ul>
        </div>
      </div>
    `,
  }),
}

