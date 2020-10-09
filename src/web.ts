import { WebPlugin } from '@capacitor/core';
import {
  SmsItem,
  SmsReaderOptions,
  SmsReaderPlugin,
  SmsReaderResponse,
} from './definitions';

export class SmsReaderWeb extends WebPlugin implements SmsReaderPlugin {
  private mockedItems: SmsItem[] = [];

  constructor() {
    super({
      name: 'SmsReader',
      platforms: ['web'],
    });
  }

  public read(_: SmsReaderOptions): Promise<SmsReaderResponse> {
    return Promise.resolve({
      items: [...this.mockedItems],
    });
  }

  public mockSmsList(items: SmsItem[]): Promise<void> {
    this.mockedItems = items;
    return Promise.resolve();
  }
}

const SmsReader = new SmsReaderWeb();

export { SmsReader };

import { registerWebPlugin } from '@capacitor/core';
registerWebPlugin(SmsReader);
