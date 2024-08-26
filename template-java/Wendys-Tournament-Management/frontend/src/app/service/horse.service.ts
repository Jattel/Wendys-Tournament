import {HttpClient, HttpParams} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {map, Observable, tap} from 'rxjs';
import {environment} from 'src/environments/environment';
import {Horse, HorseListDto} from '../dto/horse';
import {HorseSearch} from '../dto/horse';
import {formatIsoDate} from '../util/date-helper';

const baseUri = environment.backendUrl + '/horses';

@Injectable({
  providedIn: 'root'
})
export class HorseService {

  constructor(
    private http: HttpClient,
  ) { }
  /**
   * Gets horse from the system by the given ID.
   *
   * @param id the horse's id in the system
   * @return an Observable for the horse with the given id
   */
  getById(id: number | undefined): Observable<Horse> {
    console.log('Getting horse detail with the ID:' + id);
    return this.http.get<Horse>(`${baseUri}/${id}`);
  }


  search(searchParams: HorseSearch): Observable<HorseListDto[]> {
    if (searchParams.name === '') {
      delete searchParams.name;
    }
    let params = new HttpParams();
    if (searchParams.name) {
      params = params.append('name', searchParams.name);
    }
    if (searchParams.sex) {
      params = params.append('sex', searchParams.sex);
    }
    if (searchParams.bornEarliest) {
      params = params.append('bornEarliest', formatIsoDate(searchParams.bornEarliest));
    }
    if (searchParams.bornLastest) {
      params = params.append('bornLatest', formatIsoDate(searchParams.bornLastest));
    }
    if (searchParams.breedName) {
      params = params.append('breed', searchParams.breedName);
    }
    if (searchParams.limit) {
      params = params.append('limit', searchParams.limit);
    }
    return this.http.get<HorseListDto[]>(baseUri, { params })
      .pipe(tap(horses => horses.map(h => {
        h.dateOfBirth = new Date(h.dateOfBirth); // Parse date string
      })));
  }

  /**
   * Create a new horse in the system.
   *
   * @param horse the data for the horse that should be created
   * @return an Observable for the created horse
   */
  create(horse: Horse): Observable<Horse> {
    return this.http.post<Horse>(
      baseUri,
      horse
    );
  }
  /**
   * Updates horse with the given id in the system.
   *
   * @param id horse's id
   * @param horse the data for the horse that should be updated
   * @return an Observable for the updated horse
   */
  update(id: number | undefined, horse: Horse): Observable<Horse> {
    console.log('Editing horse with the ID:' + id);
    return this.http.put<Horse>(
      `${baseUri}/${id}`,
      horse
    );
  }

  delete(horseId: number | undefined):Observable<any> {
    console.log('Deleting horse with the ID:' + horseId);
    return this.http.delete<any>(baseUri + '/' + horseId);
  }
}
