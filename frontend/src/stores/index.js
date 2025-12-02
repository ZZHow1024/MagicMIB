import { createPinia } from 'pinia'

const pinia = createPinia()

export default pinia

export * from './modules/terminal.js'
export * from './modules/authentication.js'
