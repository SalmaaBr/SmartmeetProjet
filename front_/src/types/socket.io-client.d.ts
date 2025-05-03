declare module 'socket.io-client' {
  export interface Socket {
    connected: boolean;
    disconnect(): void;
    emit(event: string, ...args: any[]): boolean;
    on(event: string, callback: (...args: any[]) => void): Socket;
  }

  export function io(url: string, options?: any): Socket;
}
