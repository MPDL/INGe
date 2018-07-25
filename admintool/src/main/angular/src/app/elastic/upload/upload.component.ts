import { Component, OnInit, ViewChild, OnDestroy } from '@angular/core';
import { HttpClient, HttpResponse, HttpEventType, HttpHeaders } from '@angular/common/http';
import { Observable, Subscription } from 'rxjs';
import { MessagesService } from '../../base/services/messages.service';
import { AuthenticationService } from '../../base/services/authentication.service';

@Component({
  selector: 'app-upload',
  templateUrl: './upload.component.html',
  styleUrls: ['./upload.component.scss']
})
export class UploadComponent implements OnInit, OnDestroy {

  @ViewChild('fileInput')
  selectedFile: any;

  file2upload: any;
  filesToUpload: File[] = [];
  showFile = false;
  fileUploads: Observable<string[]>;
  selectedImage: any;
  progress: { percentage: number } = { percentage: 0 };
  isInProgress = false;
  loginSubscription: Subscription;
  token: string;

  constructor(private http: HttpClient,
    private message: MessagesService,
    private login: AuthenticationService) { }

  ngOnInit() {
    this.loginSubscription = this.login.token$.subscribe(token => {
      this.token = token;
    });
  }

  ngOnDestroy() {
    this.loginSubscription.unsubscribe();
  }

  onFileChange(event) {
    this.filesToUpload = event.target.files;
    /*
    const reader = new FileReader();
    reader.readAsText(event.target.files[0]);
    reader.onload = () => {
      this.file2upload = reader.result;
    }
    */
  }

  upload() {
    this.progress.percentage = 0;
    let data = new FormData();
    let files: File[] = this.filesToUpload;
    Array.from(files).forEach(f => {
      data.append("files2upload", f, f.name);
    });

    this.http.post('http://localhost:3000/files/upload', data, {
      reportProgress: true,
      observe: 'events'
    })
      .subscribe(event => {
        if (event.type === HttpEventType.UploadProgress) {
          this.isInProgress = true;
          this.progress.percentage = Math.round(100 * event.loaded / event.total);
        } else if (event instanceof HttpResponse) {
          this.message.info(event.status + ' ' + event.statusText + ' upload complete!')
          this.isInProgress = false;
          this.selectedFile.nativeElement.value = '';
          this.filesToUpload = undefined;
        }
      });
  }

  getFiles(): Observable<any> {
    if (this.token != null) {
      let headers = new HttpHeaders()
        .set('Authorization', this.token);
      return this.http.get('http://localhost:3000/files/list', {
        headers: headers
      });
    } else {
      return this.http.get('http://localhost:3000/files/list');
    }
  }

  showFiles() {
    if (this.showFile) {
      this.showFile = false;
    } else {
      this.showFile = true;
      this.fileUploads = this.getFiles();
    }
  }

  setImageSource(file) {
    this.selectedImage = file;
  }
}
