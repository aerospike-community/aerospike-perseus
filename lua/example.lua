-- count and sum reducer
local function add_values(val1, val2)
    return (val1 or 0) + (val2 or 0)
end

-- count mapper
-- note closures are used to access aggregate parameters such as bin
local function rec_to_count_closure(bin)
    local function rec_to_count(rec)
        -- if bin is specified: if bin exists in record return 1 else 0; if no bin is specified, return 1
        return (not bin and 1) or ((rec[bin] and 1) or 0)
    end
    return rec_to_count
end

-- count
function count(stream)
    return stream : map(rec_to_count_closure) : reduce(add_values)
end


-- mapper for various single bin aggregates
local function rec_to_bin_value_closure(bin)
    local function rec_to_bin_value(rec)
    -- if a numeric bin exists in record return its value; otherwise return nil
        local val = rec[bin]
        if (type(val) ~= "number") then val = nil end
        return val
    end
    return rec_to_bin_value
end

-- sum
function sum(stream, bin)
    return stream : map(rec_to_bin_value_closure(bin)) : reduce(add_values)
end

-- min reducer
local function get_min(val1, val2)
    local min = nil
    if val1 then
        if val2 then
            if val1 < val2 then min = val1 else min = val2 end
        else min = val1
        end
    else
        if val2 then min = val2 end
    end
    return min
end

-- min
function min(stream, bin)
    return stream : map(rec_to_bin_value_closure(bin)) : reduce(get_min)
end

-- max reducer
local function get_max(val1, val2)
    local max = nil
    if val1 then
        if val2 then
            if val1 > val2 then max = val1 else max = val2 end
        else max = val1
        end
    else
        if val2 then max = val2 end
    end
    return max
end

-- max
function max(stream, bin)
    return stream : map(rec_to_bin_value_closure(bin)) : reduce(get_max)
end

-- map function to compute average and range
local function compute_final_stats(stats)
    local ret = map();
    ret['Average'] = stats["sum"] / stats["count"]
    ret['Count'] = stats["count"]
    ret['Min'] = stats["min"]
    ret['Max'] = stats["max"]
    return ret
end

-- merge partial stream maps into one
local function merge_stats(a, b)
    local ret = map()
    ret["sum"] = add_values(a["sum"], b["sum"])
    ret["count"] = add_values(a["count"], b["count"])
    ret["min"] = get_min(a["min"], b["min"])
    ret["max"] = get_max(a["max"], b["max"])
    return ret
end

-- aggregate operator to compute stream state for average_range
local function aggregate_stats(agg, val)
    agg["count"] = (agg["count"] or 0) + ((val["bin_avg"] and 1) or 0)
    agg["sum"] = (agg["sum"] or 0) + (val["bin_avg"] or 0)
    agg["min"] = get_min(agg["min"], val["bin_range"])
    agg["max"] = get_max(agg["max"], val["bin_range"])
    return agg
end

-- average_range
function average_range(stream, bin)
    local function rec_to_bins(rec)
        -- extract the values of the two bins in ret
        local ret = map()
        ret["bin_avg"] = rec[bin]
        ret["bin_range"] = rec[bin]
        return ret
    end
    return stream : map(rec_to_bins) : aggregate(map(), aggregate_stats) : reduce(merge_stats) : map(compute_final_stats)
end

-- lua test
function lua_test(rec)
    local ret = map()
    if not aerospike:exists(rec) then
        return   -- Set the return status
    else
        local x = (rec['keyPlus20'] - rec['keyPlus10']) * rec['octet']
        if  x > 100  then
            rec['UDFRes'] = 'Yes'
        else
            rec['UDFRes'] = 'No'
        end

        aerospike:update(rec)
    end
end