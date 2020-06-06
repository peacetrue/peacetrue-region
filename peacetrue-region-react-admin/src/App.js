// in src/App.js
import {Admin, fetchUtils, Resource} from 'react-admin';
import {RegionList} from './modules/regions/list';
import {RegionCreate} from './modules/regions/create';
import {RegionEdit} from './modules/regions/edit';
import {RegionShow} from './modules/regions/show';
import polyglotI18nProvider from 'ra-i18n-polyglot';
import chineseMessages from 'ra-language-chinese';
import {springDataProvider, springHttpClient} from 'ra-data-spring-rest';

const i18nProvider = polyglotI18nProvider(() => chineseMessages, 'cn');

let httpClient = springHttpClient((url, options = {}) => {
    options.credentials = 'include';
    return fetchUtils.fetchJson(url, options)
        .then(response => {
            let {json} = response;
            if (json.code && json.message) {
                if (json.code !== 'success') {
                    let error = new Error(json.message);
                    error.status = 500;
                    throw error;
                }
                response.json = json.data;
            }
            return response;
        });
});

const dataProvider = springDataProvider('http://localhost:8080', httpClient);
const App = () => (
        <Admin
            i18nProvider={i18nProvider}
            dataProvider={dataProvider}>
            <Resource
                options={{label: '地区'}}
                name="regions"
                list={RegionList}
                create={RegionCreate}
                edit={RegionEdit}
                show={RegionShow}
            />
        </Admin>
    )
;

export default App;
