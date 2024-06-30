function lua_test(rec)
    local ret = map()
    if not aerospike:exists(rec) then
        return   -- Set the return status
    else
        local x = (rec['keyPlus10'] - rec['keyN'])
        if  x == 100  then
            rec['UDFRes'] = 'Yes'
        else
            rec['UDFRes'] = 'No'
        end

        aerospike:update(rec)
    end
end