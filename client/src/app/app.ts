import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { SharedModule } from './shared/shared.module';
import { NotificationToastComponent } from './shared/components/notification-toast/notification-toast.component';



@Component({
  selector: 'app-root',
  imports: [RouterOutlet, SharedModule, NotificationToastComponent],
  templateUrl: './app.html',
  styleUrl: './app.scss'
})
export class App {
  protected readonly title = 'taskboard-pro';
}
