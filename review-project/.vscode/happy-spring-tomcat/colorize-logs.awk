function has_level(level, header, pattern) {
    # Timestamp layouts vary, so inspect only a bounded header for an uppercase level token.
    header = substr(line, 1, 160)
    pattern = "(^|[^[:alnum:]_])" level "([^[:alnum:]_]|$)"
    return normalized ~ ("\"level\"[[:space:]]*:[[:space:]]*\"" tolower(level) "\"") || header ~ pattern
}
BEGIN {
    c_reset = "\033[0m"
    c_dim = "\033[2m"
    c_magenta = "\033[35m"
    c_red = "\033[31m"
    c_yellow = "\033[33m"
    c_green = "\033[32m"
    c_cyan = "\033[36m"
    c_dark_cyan = "\033[36m"
    c_blue = "\033[34m"
    c_orange = "\033[38;5;208m"
    c_white = "\033[37m"
    current_color = c_white
}
{
    line = $0
    if (length(line) > 16384) {
        print line
        fflush(); next
    }
    sub(/^\xef\xbb\xbf/, "", line)
    gsub(/\033\[[0-9;]*m/, "", line)
    normalized = tolower(line)
    is_new_entry = has_level("TRACE") || has_level("DEBUG") || has_level("INFO") || has_level("WARN") || has_level("WARNING") || has_level("ERROR") || has_level("FATAL") || has_level("SEVERE") || has_level("CRITICAL")
    matched = 0

    if (normalized ~ /^[[:space:]]+at[[:space:]]+[^[:space:]]+\([^)]*\)[[:space:]]*$/) {
        printf "%s%s%s%s\n", current_color, c_dim, line, c_reset
        fflush(); next
    }
    if (normalized ~ /^[[:space:]]*\.\.\.[[:space:]]+[0-9]+[[:space:]]+(more|common frames omitted)[[:space:]]*$/) {
        printf "%s%s%s%s\n", c_dark_cyan, c_dim, line, c_reset
        fflush(); next
    }

    if (normalized ~ /^[[:space:]]*caused by:/) { current_color = c_red; matched = 1 }
    else if (normalized ~ /^[[:space:]]*suppressed:/) { current_color = c_yellow; matched = 1 }
    else if (has_level("FATAL") || has_level("CRITICAL")) { current_color = c_magenta; matched = 1 }
    else if (has_level("ERROR") || has_level("SEVERE")) { current_color = c_red; matched = 1 }
    else if (has_level("WARN") || has_level("WARNING")) { current_color = c_yellow; matched = 1 }
    else if (has_level("INFO")) { current_color = c_cyan; matched = 1 }
    else if (has_level("DEBUG")) { current_color = c_blue; matched = 1 }
    else if (has_level("TRACE")) { current_color = c_dark_cyan; matched = 1 }
    else {
        if (normalized ~ /^[[:space:]]*(exception in thread|[[:alnum:]_.$]+(exception|error)(:|$))|심각/) { current_color = c_red; matched = 1 }
        else if (normalized ~ /warn|warning|potential|경고/) { current_color = c_yellow; matched = 1 }
        else if (normalized ~ /sql|query|sqltiming|hikaripool|hibernate:|preparing:|parameters:/) { current_color = c_orange; matched = 1 }
        else if (normalized ~ /http|request|response|mapping|dispatching/) { current_color = c_green; matched = 1 }
        else if (normalized ~ /started|initializing|정보/) { current_color = c_cyan; matched = 1 }
    }

    if (matched || is_new_entry) {
        if (!matched) { current_color = c_white }
        printf "%s%s%s\n", current_color, line, c_reset
    } else {
        printf "%s%s%s%s\n", current_color, c_dim, line, c_reset
    }
    fflush()
}