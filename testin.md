:deep(.welcome-panels) {
  position: relative;
  overflow: hidden;
}

:deep(.welcome-panels .sv-tabs__panel) {
  display: block !important;
  width: 100%;
  position: absolute;
  top: 0;
  left: 0;
  transition: transform 0.35s ease;
  transform: translateX(calc((var(--panel-index, 0) - var(--active-tab-index, 0)) * 100%));
  pointer-events: none;
}

:deep(.welcome-panels .sv-tabs__panel--active) {
  position: relative;
  pointer-events: auto;
}
