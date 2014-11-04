import re

# If input file is seperated by TAB.
TABED = True

STOPWORD_33 = {}
with open("../resource/english_33.txt") as f:
    for line in f:
        STOPWORD_33[line.strip()] = True
with open("../resource/english_stop_words_smart.txt") as f:
    for line in f:
        STOPWORD_33[line.strip()] = True
with open("../resource/sw_ranksnl.txt") as f:
    for line in f:
        STOPWORD_33[line.strip()] = True

_ABBRE = [
        # Common abbrevations (upper-case or with special character).
        'A.B.', 'abbr.', 'Acad.', 'A.D.', 'alt.', 'A.M.', 'AM', 'Assn.', 'at. no.', 'at. wt.',
        'Aug.', 'Ave.', 'AWOL', 'b.', 'B.A.', 'B.C.', 'b.p.', 'B.S.', 'Btu', 'C', 'c.', 'cal',
        'Capt.', 'cent.', 'cm', 'co.', 'Col.', 'Comdr.', 'Corp.', 'Cpl.', 'cu', 'd.', 'D.C.',
        'Dec.', 'dept.', 'dist.', 'div.', 'Dr.', 'E', 'ed.', 'est.', 'et al.', 'F', 'Feb.',
        'fl.', 'fl oz', 'FM', 'ft', 'gal.', 'Gen.', 'GMT', 'GNP', 'GOP', 'Gov.', 'grad.',
        'H', 'Hon.', 'hr', 'i.e.', 'in.', 'inc.', 'Inst.', 'IRA', 'IRS', 'Jan.', 'Jr.',
        'K', 'kg', 'km', 'lat.', 'lb', 'Lib.', 'long.', 'Lt.', 'Ltd.', 'M', 'M.D.',
        'mg', 'mi', 'min', 'mm', 'mph', 'Mr.', 'Mrs.', 'Msgr', 'mt.', 'mts.', 'Mus.', 'N',
        'NAACP', 'NASA', 'NATO', 'NE', 'no.', 'Nov.', 'OAS', 'Oct.', 'Op.', 'oz', 'pl.',
        'pop.', 'pseud.', 'pt.', 'pt', 'pub.', 'qt', 'Rev.', 'rev.', 'R.N.', 'rpm', 'RR',
        'S', 'SEATO', 'SEC', 'sec', 'Sept.', 'Ser.', 'Sgt.', 'sq', 'Sr.', 'SSR', 'St.',
        'UNICEF', 'uninc.', 'Univ.', 'U.S.', 'US', 'USA', 'USAF', 'USCG', 'USMC', 'USN', 'USSR', 'VFW',
        'VISTA', 'vol.', 'vs.', 'W', 'WHO', 'wt.', 'yd', 'YMCA', 'YWCA',
        'a.m.', 'a.m', 'p.m.', 'p.m', 'Ph.D', 'e.g.', 'i.e.', 'U.N',
        # Common upper-case 
        # Month
        'Jan.', 'January', 'Feb.', 'February', 'Mar.', 'March', 'Apr.', 'April',
        'May', 'June', 'July', 'Aug.', 'August', 'Sept.', 'September', 'Oct.',
        'October', 'Nov.', 'November', 'Dec.', 'December',
        # Week
        'Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday', 'Sunday',
        # Country (single word entities only)
        'America', 'English', 'England', 'Afghanistan', 'Albania', 'Algeria',
        'Andorra', 'Angola', 'Antigua', 'Argentina', 'Armenia', 'Australia', 'Austria',
        'Azerbaijan', 'Bahamas', 'Bahrain', 'Bangladesh', 'Barbados', 'Belarus',
        'Belgium', 'Belize', 'Benin', 'Bhutan', 'Bolivia', 'Botswana', 'Brazil',
        'Brunei', 'Bulgaria', 'Burkina', 'Burundi', 'Cambodia', 'Cameroon',
        'Canada', 'Chad', 'Chile', 'China', 'Colombia', 'Comoros', 'Congo',
        'Croatia', 'Cuba', 'Cyprus', 'Czech', 'Denmark', 'Djibouti', 'Dominica',
        'Dominican', 'East Timor', 'Ecuador', 'Egypt', 'Eritrea', 'Estonia',
        'Ethiopia', 'Fiji', 'Finland', 'France', 'Gabon', 'Gambia', 'Georgia',
        'Germany', 'Ghana', 'Greece', 'Grenada', 'Guatemala', 'Guinea',
        'Guinea-Bissau', 'Guyana', 'Haiti', 'Honduras', 'Hungary', 'Iceland',
        'India', 'Indonesia', 'Iran', 'Iraq', 'Ireland', 'Israel', 'Italy',
        'Jamaica', 'Japan', 'Jordan', 'Kazakhstan', 'Kenya', 'Kiribati', 'Korea',
        'Kosovo', 'Kuwait', 'Kyrgyzstan', 'Laos', 'Latvia', 'Lebanon', 'Lesotho',
        'Liberia', 'Libya', 'Liechtenstein', 'Lithuania', 'Luxembourg',
        'Macedonia', 'Madagascar', 'Malawi', 'Malaysia', 'Maldives', 'Mali',
        'Malta', 'Marshall Islands', 'Mauritania', 'Mauritius', 'Mexico',
        'Micronesia', 'Moldova', 'Monaco', 'Mongolia', 'Montenegro', 'Morocco',
        'Mozambique', 'Myanmar', 'Namibia', 'Nauru', 'Nepal', 'Netherlands',
        'Nicaragua', 'Niger', 'Nigeria', 'Norway', 'Oman', 'Pakistan', 'Palau',
        'Panama', 'Paraguay', 'Peru', 'Philippines', 'Poland', 'Portugal',
        'Qatar', 'Romania', 'Russian', 'Rwanda', 'Samoa', 'Senegal', 'Serbia',
        'Seychelles', 'Singapore', 'Slovakia', 'Slovenia', 'Solomon', 'Somalia',
        'Spain', 'Sudan', 'Suriname', 'Swaziland', 'Sweden', 'Switzerland',
        'Syria', 'Taiwan', 'Tajikistan', 'Tanzania', 'Thailand', 'Togo', 'Tonga',
        'Trinidad', 'Tobago', 'Tunisia', 'Turkey', 'Turkmenistan', 'Tuvalu',
        'Uganda', 'Ukraine', 'Uruguay', 'Uzbekistan', 'Vanuatu', 'Venezuela',
        'Vietnam', 'Yemen', 'Zambia', 'Zimbabwe',
        # People
        'American', 'Canadian', 'Chinese', 'Japanese', 'Australian',
        # Website
        'Google', 'Yahoo!', 'Yahoo', 'yahoo.com', 'hotmail.com', 'Google.com'
]
ABBRE = {}
for token in _ABBRE:
    ABBRE[token] = True

def deep_clean(in_file, out_file):
    """ Preprocess the documents based on the following rules:
        1. Tokenize the documents by \s characters.
        2. Remove token which contains no letter or any digit.
        3. No further change for abbrevations or special names.
        4. Tokens with special (non-letter) characters:
            4.1. If special characters exist in the beginning or the ending of
                 the token, simply remove these special characters.
            4.2. If is "(/|-|_)" exists in the middle of the token, separate the
                 token by "-" and remove "(/|-|_)".
            4.3. If is "'" exists in the middle of the token:
                 4.3.1. after "'" is a common pattern ("'s", "'ve"), then remove
                        "'" and its following characters;
                 4.3.2.  after "'" is NOT a common pattern, then remove "'" only.
            4.4. Remove the "," in the numeric token.
        5. Lower case the normal tokens.
    """
    if TABED:
        pairs = [line.strip() for line in open(in_file)]
        content = []
        for pair in pairs:
            content.extend(pair.split('\t'))
    else:
        content = [line.strip() for line in open(in_file)]

    bits = len(str(len(content)))

    p_delimiters  = re.compile(r'[()\"/_,\s-]+')
    p_not_remove1 = re.compile(r'[a-zA-Z]')
    p_not_remove2 = re.compile(r'\d')
    p_trim        = re.compile(r'^[^a-zA-Z0-9]*(.*?)[^a-zA-Z0-9]*$')
    p_tick_rm     = re.compile(r'(.*?)\'(s|ve)$')
    p_tick_cb     = re.compile(r'[\']')
    p_special     = re.compile(r'[a-zA-Z0-9][^a-zA-Z0-9]+[a-zA-Z0-9]')
    p_comma_num   = re.compile(r'^[\d,]+$')

    import sys
    with open(out_file, 'w') as file:
        for i in range(0, len(content)):
            line = content[i]
            output = ''
            special = False
            # Rule 1, 4.2
            tokens = p_delimiters.split(line)
            for token in tokens:
                orig = token
                try:
                    # Rule 3
                    ABBRE[token]
                    output += ('' if len(output) == 0 else ' ') + token
                except KeyError:
                    # Rule 2
                    if p_not_remove1.search(token) != None and p_not_remove2.search(token) == None:
                        # Rule 4.1
                        token = p_trim.match(token).group(1)
                        # Rule 4.3.1
                        m = p_tick_rm.match(token)
                        if m != None:
                            token = m.group(1)
                        # Rule 4.3.2
                        token = ''.join(p_tick_cb.split(token))
                        # Rule 4.4
                        if p_comma_num.match(token):
                            token = re.sub(',', '', token)
                        token = token.lower()

                        if p_special.search(token): # Debug
                            print '[ERROR] \"' + token + '\" (' + orig + ') in line ' + str(i) + ' (file ' + str(i+1) + ').'
                            special = True

                        try:
                            STOPWORD_33[token]
                        except KeyError:
                            output += ('' if len(output) == 0 else ' ') + token

            if special:
                pass
                #print '[ERROR] ' + line
            
            # Write line information to file.
            file.write(output + '\n')



if __name__ == '__main__':
    import sys
    reload(sys)
    sys.setdefaultencoding('utf-8')

    # STS 12'
    train_dir = '../data/SemEval2012/'
    input_files = [
        'STS.input.MSRpar.txt',
        'STS.input.MSRvid.txt',
        'STS.input.SMTeuroparl.txt',
        'STS.input.surprise.OnWN.txt',
        'STS.input.surprise.SMTnews.txt'
    ]
    for input_file in input_files:
        deep_clean(train_dir + input_file, train_dir + input_file[:-4] + '.clean.txt')

    # STS 13'
    train_dir = '../data/SemEval2013/'
    input_files = [
        'STS.input.FNWN.txt',
        'STS.input.headlines.txt',
        'STS.input.OnWN.txt',
        'STS.input.SMT.txt'
    ]
    for input_file in input_files:
        deep_clean(train_dir + input_file, train_dir + input_file[:-4] + '.clean.txt')

