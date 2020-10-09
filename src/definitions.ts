declare module '@capacitor/core' {
  interface PluginRegistry {
    SmsReader: SmsReaderPlugin;
  }
}

export interface SmsReaderResponse {
  items: SmsItem[];
}

export interface SmsItem {
  id: number;
  threadId: number;
  address: string;
  date: number;
  dateSent: number;
  protocol: number;
  read: number;
  status: number;
  type: number;
  replyPathPresent: number;
  body: string;
  locked: number;
  subId: number;
  errorCode: number;
  creator: string;
  seen: number;
}

export interface SmsReaderOptions {
  skip: number;
  take: number;
}

export interface SmsReaderPlugin {
  read(options: SmsReaderOptions): Promise<SmsReaderResponse>;
}
