import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';

import { FlatTreeControl } from '@angular/cdk/tree';
import { MatTreeFlatDataSource, MatTreeFlattener } from '@angular/material/tree';
import { Observable, Subscription } from 'rxjs';
import { OrganizationsService } from '../services/organizations.service';
import { AuthenticationService } from '../../base/services/authentication.service';
import { MessagesService } from '../../base/services/messages.service';
import { environment } from '../../../environments/environment';
// import { OrganizationTreeService, OUTreeFlatNode, OUTreeNode } from '../services/organization-tree.service';
import { OrganizationTree2Service, OUTreeNode, OUTreeFlatNode } from '../services/organization-tree2.service';

@Component({
  selector: 'app-organization-tree',
  templateUrl: 'organization-tree.component.html',
  styleUrls: ['organization-tree.component.scss'],
  providers: [OrganizationTree2Service]
})
export class OrganizationTreeComponent implements OnInit, OnDestroy {
  ounames: any[] = [];
  subscription: Subscription;
  token;
  selected: any;
  searchTerm;
  nodeMap: Map<string, OUTreeFlatNode> = new Map<string, OUTreeFlatNode>();
  treeControl: FlatTreeControl<OUTreeFlatNode>;
  treeFlattener: MatTreeFlattener<OUTreeNode, OUTreeFlatNode>;
  dataSource: MatTreeFlatDataSource<OUTreeNode, OUTreeFlatNode>;

  constructor(private database: OrganizationTree2Service,
    private router: Router,
    private loginService: AuthenticationService,
    private service: OrganizationsService,
    private message: MessagesService) { }

  ngOnInit() {
    this.subscription = this.loginService.token$.subscribe(token => {
      this.token = token;
    });
    this.treeFlattener = new MatTreeFlattener(this.transformer, this.getLevel,
      this.isExpandable, this.getChildren);

    this.treeControl = new FlatTreeControl<OUTreeFlatNode>(this.getLevel, this.isExpandable);

    this.dataSource = new MatTreeFlatDataSource(this.treeControl, this.treeFlattener);

    this.database.dataChange.subscribe(data => {
      this.dataSource.data = data;
    });

    this.database.initialize();
  }

  ngOnDestroy() {
    this.subscription.unsubscribe();
  }

  getChildren = (node: OUTreeNode): Observable<OUTreeNode[]> => { return node.childrenChange; };

  transformer = (node: OUTreeNode, level: number) => {
    if (this.nodeMap.has(node.ouName)) {
      return this.nodeMap.get(node.ouName)!;
    }
    const newNode = new OUTreeFlatNode(node.ouName, node.ouId, level, node.hasChildren, node.parentOUId);
    this.nodeMap.set(node.ouName, newNode);
    return newNode;
  }

  getLevel = (node: OUTreeFlatNode) => { return node.level; };

  isExpandable = (node: OUTreeFlatNode) => { return node.expandable; };

  hasChild = (_: number, _nodeData: OUTreeFlatNode) => { return _nodeData.expandable; };

  loadChildren(node: OUTreeFlatNode) {
    this.database.loadChildren(node.ouName, node.ouId);
  }

  gotoDetails(node) {
    const id: string = node.ouId;
    this.router.navigate(['/organization', id]);
  }

  addNewOrganization() {
    const id = 'new org';
    this.router.navigate(['/organization', id]);
  }

  onSelect(ou: any) {
    const id: string = ou.objectId;
    this.router.navigate(['/organization', id]);
  }

  isSelected(ou) {
    return true;
  }

  getNames(term) {
    if (term.length > 0 && !term.startsWith('"')) {
      this.returnSuggestedOUs(term);
    } else if (term.length > 3 && term.startsWith('"') && term.endsWith('"')) {
      this.returnSuggestedOUs(term);
    }
  }

  returnSuggestedOUs(term) {
    const ouNames: any[] = [];
    const url = environment.rest_url + environment.rest_ous;
    const queryString = '?q=metadata.name.auto:' + term;
    this.service.filter(url, null, queryString, 1)
      .subscribe(res => {
        res.list.forEach(ou => {
          ouNames.push(ou);
        });
        if (ouNames.length > 0) {
          this.ounames = ouNames;
        } else {
          this.ounames = [];
        }
      }, err => {
        this.message.error(err);
      });
  }

  close() {
    this.searchTerm = '';
    this.ounames = [];
  }

  select(term) {
    this.searchTerm = term.metadata.name;
    this.router.navigate(['/organization', term.objectId]);
    this.ounames = [];
  }

}
