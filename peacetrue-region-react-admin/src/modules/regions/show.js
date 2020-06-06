import {Show, SimpleShowLayout, TextField} from 'react-admin';

export const RegionShow = (props) => {
    console.info('RegionShow:', props);
    return (
        < Show
    {...
        props
    }
    title = {`${props.options.label}#${props.id}`
}>
<
    SimpleShowLayout >
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
        < /SimpleShowLayout>
        < /Show>
)
    ;
};
