import {Datagrid, EditButton, Filter, List, TextField, TextInput} from 'react-admin';

const Filters = (props) => (
    < Filter
{...
    props
}
>
<
TextInput
label = {'编码'}
source = "code"
allowEmpty
alwaysOn / >
< TextInput
label = {'名称'}
source = "name"
allowEmpty
alwaysOn / >
< TextInput
label = {'备注'}
source = "remark"
allowEmpty
alwaysOn / >
< /Filter>
)
;

export const RegionList = props => {
    console.info('RegionList:', props);
    return (
        < List
    {...
        props
    }
    title = {`${props.options.label}列表`
}
    filters = { < Filters / >
}
    sort = {
    {
        field: 'createdTime', order
    :
        'desc'
    }
}>
<
    Datagrid
    rowClick = "show" >
        < TextField
    label = {'编码'}
    source = "code" / >
        < TextField
    label = {'名称'}
    source = "name" / >
        < TextField
    label = {'备注'}
    source = "remark" / >
        < TextField
    label = {'上一级主键'}
    source = "parentId" / >
        < EditButton / >
        < /Datagrid>
        < /List>
)
};
