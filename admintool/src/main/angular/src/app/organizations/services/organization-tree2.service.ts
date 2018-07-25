import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { OrganizationsService } from '../services/organizations.service';
import { MessagesService } from '../../base/services/messages.service';


export class OUTreeNode {
  childrenChange: BehaviorSubject<OUTreeNode[]> = new BehaviorSubject<OUTreeNode[]>([]);

  get children(): OUTreeNode[] {
    return this.childrenChange.value;
  }

  constructor(public ouName: string,
    public ouId: string,
    public hasChildren: boolean = false,
    public parentOUId: string | null = null) { }
}

export class OUTreeFlatNode {
  constructor(public ouName: string,
    public ouId: string,
    public level: number = 1,
    public expandable: boolean = false,
    public parentOUId: string | null = null) { }
}

@Injectable({
  providedIn: 'root'
})
export class OrganizationTree2Service {

  dataChange: BehaviorSubject<OUTreeNode[]> = new BehaviorSubject<OUTreeNode[]>([]);
  nodeMap: Map<string, OUTreeNode> = new Map<string, OUTreeNode>();

  get data(): OUTreeNode[] { return this.dataChange.value; }

  constructor(private service: OrganizationsService,
    private message: MessagesService) {
    // this.initialize();
  }

  async initialize() {
    const data: any[] = [];
    try {
      const mpg = await this.service.getOuById('ou_persistent13', null).toPromise();
      const ext = await this.service.getOuById('ou_persistent22', null).toPromise();
      data.push(this.generateNode(mpg));
      data.push(this.generateNode(ext));
      this.dataChange.next(data);
    } catch (e) {
      this.message.error(e);
    }

  }

  getChildren4OU(id) {
    const resp = this.service.listChildren4Ou(id, null).toPromise();
    return resp;
  }

  loadChildren(ouName: string, ouId: string) {
    if (!this.nodeMap.has(ouName)) {
      return;
    }
    const parent = this.nodeMap.get(ouName)!;
    let children = [];
    this.getChildren4OU(ouId)
      .then(resp => {
        children = resp;
        const nodes = children.map(child => this.generateNode(child));
        parent.childrenChange.next(nodes);
        this.dataChange.next(this.dataChange.value);
      }).catch(err => {
        this.message.error(err);
      });
  }

  private generateNode(ou: any): OUTreeNode {
    if (this.nodeMap.has(ou.name)) {
      return this.nodeMap.get(ou.name)!;
    }
    const result = new OUTreeNode(ou.name, ou.objectId, ou.hasChildren);
    this.nodeMap.set(ou.name, result);
    return result;
  }
}

