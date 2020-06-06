import {Resource} from "react-admin";

import {RegionList} from './list';
import {RegionCreate} from './create';
import {RegionEdit} from './edit';
import {RegionShow} from './show';

let Region = {list: RegionList, create: RegionCreate, edit: RegionEdit, show: RegionShow};
export const RegionResource = props => (
    < Resource
options = {
{
    label: '地区'
}
}
name = "regions"
{...
    Region
}
/>
)
;
export default Region;
