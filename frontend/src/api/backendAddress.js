const dev = true

const hostname = dev ? 'localhost' : window.location.hostname
const port = dev ? '8080' : window.location.port

export const baseURL = 'http://' + hostname + ':' + port
