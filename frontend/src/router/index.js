import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      redirect: '/mib',
      component: () => import('@/layout/IndexLayout.vue'),
      children: [
        {
          path: '/mib',
          name: 'mib-page',
          component: () => import('@/views/MibPage/MibPage.vue'),
        },
        {
          path: '/terminal',
          name: 'terminal-page',
          component: () => import('@/views/TerminalPage/TerminalPage.vue'),
        },
        {
          path: '/about',
          name: 'about-page',
          component: () => import('@/views/AboutPage/AboutPage.vue'),
        },
      ],
    },
  ],
})

export default router
