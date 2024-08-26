import {Component, OnInit} from '@angular/core';
import {Horse} from "../../../dto/horse";
import {Sex} from "../../../dto/sex";
import {HorseService} from "../../../service/horse.service";
import {ActivatedRoute, Router} from "@angular/router";
import {ToastrService} from "ngx-toastr";
import {Breed} from "../../../dto/breed";
import {of} from "rxjs";
import {BreedService} from "../../../service/breed.service";


@Component({
  selector: 'app-horse-detail',
  templateUrl: './horse-detail.component.html',
  styleUrl: './horse-detail.component.scss'
})
export class HorseDetailComponent implements OnInit {
  id: number | undefined;
  error = false;
  horse: Horse = {
    height: 0,
    weight: 0,
    name: '',
    dateOfBirth: new Date(),
    sex: Sex.female
  };
  private heightSet: boolean = false;
  private weightSet: boolean = false;
  private dateOfBirthSet: boolean = false;

  public formatBreedName(breed: Breed | null): string {
    return breed?.name ?? '';
  }

  breedSuggestions = (input: string) => (input === '')
    ? of([])
    :  this.breedService.breedsByName(input, 5);

  get height(): number | null {
    return this.heightSet
      ? this.horse.height
      : null;
  }

  set height(value: number) {
    this.heightSet = true;
    this.horse.height = value;
  }

  get weight(): number | null {
    return this.weightSet
      ? this.horse.weight
      : null;
  }

  set weight(value: number) {
    this.weightSet = true;
    this.horse.weight = value;
  }

  get dateOfBirth(): Date | null {
    return this.dateOfBirthSet
      ? this.horse.dateOfBirth
      : null;
  }

  set dateOfBirth(value: Date) {
    this.dateOfBirthSet = true;
    this.horse.dateOfBirth = value;
  }
  constructor(
    private service: HorseService,
    private router: Router,
    private breedService: BreedService,
    private route: ActivatedRoute,
    private notification: ToastrService,
  ) {
  }

  public get heading(): string {
    return 'Details New Horse';
  }



  ngOnInit(): void {
    this.route.data.subscribe(data => {
      this.id = this.route.snapshot.params.id;
      this.getHorseById();
      console.log(`ID: ${this.id}`);
      this.service.getById(this.id).subscribe(
        (newHorse: Horse) => {
          this.horse.name = newHorse.name;
          this.height = newHorse.height;
          this.horse.sex = newHorse.sex;
          this.dateOfBirth = newHorse.dateOfBirth;
          this.horse.breed = newHorse.breed;
          this.weight = newHorse.weight;
        },
        error => {
          console.error('Error getting horse', error);
          error.error.errors.forEach((el: string | undefined) => {
            this.notification.error(el);
          });
          this.router.navigate(['/horses']);
        }
      );
    });
  }

  goToEdit() {
    this.router.navigate(['/horses/edit/' + this.id]);
  }

  delete() {
    const observable = this.service.delete(this.horse.id);
    observable.subscribe({
      next: data => {
        this.notification.success(`Horse ${this.horse.name} successfully deleted.`);
        this.router.navigate(['/horses']);
      },
      error: (error: any) => {
        console.error('Error deleting horse', error);
        this.notification.error(error.error.errors);
      }
    });
  }

  getHorseById(): void {
    this.service.getById(this.id)
      .subscribe((horse: any) => this.horse = horse);
  }

}

