declare module 'sockjs-client' {
  interface SockJSOptions {
    server?: string;
    transports?: string | string[];
    sessionId?: number | (() => number);
    heartbeat?: number;
    timeout?: number;
  }

  class SockJS {
    constructor(url: string, _reserved?: any, options?: SockJSOptions);
    close(code?: number, reason?: string): void;
    send(data: string): void;
    onopen: (() => void) | null;
    onclose: ((e: { code: number; reason: string; wasClean: boolean }) => void) | null;
    onmessage: ((e: { data: string }) => void) | null;
    onerror: ((e: Error) => void) | null;
  }

  export default SockJS;
}
