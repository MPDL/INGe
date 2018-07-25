import { AdmintoolPage } from './app.po';

describe('admintool App', () => {
  let page: AdmintoolPage;

  beforeEach(() => {
    page = new AdmintoolPage();
  });

  it('should display message saying app works', () => {
    page.navigateTo();
    expect(page.getParagraphText()).toEqual('app works!');
  });
});
