import { Component, EventEmitter, Output } from '@angular/core';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-post-filter',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './post-filter.component.html',
  styleUrls: ['./post-filter.component.css'],
})
export class PostFilterComponent {
  @Output() filterChanged = new EventEmitter<any>(); // EventEmitter to send data to the parent component

  startDate: string | null = null;
  endDate: string | null = null;
  author: string | null = null;
  keyword: string | null = null;

  // Method to emit filter data when the user interacts with the filter form
  emitFilter() {
    const filter = {
      startDate: this.startDate,
      endDate: this.endDate,
      author: this.author,
      keyword: this.keyword,
    };
    this.filterChanged.emit(filter); // Emit the filter data to the parent
  }
}
