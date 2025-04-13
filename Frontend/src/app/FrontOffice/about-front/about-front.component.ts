import { Component , OnInit} from '@angular/core';
import * as AOS from 'aos';

@Component({
  selector: 'app-about-front',
  templateUrl: './about-front.component.html',
  styleUrls: ['./about-front.component.css']
})
export class AboutFrontComponent implements OnInit{
  constructor() {}

    ngOnInit(): void {
      AOS.init();
    }

}
