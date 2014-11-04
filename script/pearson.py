from math import sqrt
import sys

def _avg(data):
    sum = 0
    for val in data:
        sum += val
    return float(sum) / len(data)

def _pearson(data1, data2):
    avg1 = _avg(data1)
    avg2 = _avg(data2)
    nom = 0
    denom1 = 0
    denom2 = 0
    for i in range(0, len(data1)):
        diff1 = data1[i] - avg1
        diff2 = data2[i] - avg2
        nom += diff1 * diff2
        denom1 += diff1 * diff1
        denom2 += diff2 * diff2
    return nom / (sqrt(denom1) * sqrt(denom2))

def _data(file_path, type=None):
    if type == None:
        type = file_path[-3:]
    if type == 'csv':
        return _csv_to_data(file_path)
    if type == 'tsv':
        return _tsv_to_data(file_path)
    else:
        return _tsv_to_data(file_path)

def _tsv_to_data(file_path):
    with open(file_path, 'r') as f:
        data = [float(line.strip()) for line in f.readlines()]
    return data

def _csv_to_data(file_path):
    data = []
    with open(file_path, 'r') as f:
        for line in f.readlines():
            content = line.translate(None, ',').strip()
            if len(content) > 0:
                data.append(float(content))
            else:
                data.append(0)
    return data

def pearson(data_file, data_type, gs_file, gs_type):
    return _pearson(_data(data_file, data_type), _data(gs_file, gs_type))

def evaluate_old(dataset_path):
    if dataset_path[-1] != '/':
        dataset_path += '/'
    return dataset_path.split(r'/')[-2] + '\n' \
            + 'Original: ' + str(pearson(dataset_path + 'original.txt', 'csv')) + '\n' \
            + 'Option 1: ' + str(pearson(dataset_path + 'option1.txt', 'csv')) + '\n' \
            + 'Option 2: ' + str(pearson(dataset_path + 'option2.txt', 'csv')) + '\n' \
            + 'Option 3: ' + str(pearson(dataset_path + 'option3.txt', 'csv')) + '\n' \
            + 'Option 4: ' + str(pearson(dataset_path + 'option4.txt', 'csv')) + '\n' 

def evaluate(dataset_path, data_type, gs_file, gs_type):
    if dataset_path[-1] != '/':
        dataset_path += '/'
    return dataset_path.split(r'/')[-2] + '\n' \
            + '         | original | remove 0 | balanced\n' \
            + 'Original |  ' + str(pearson(dataset_path + 'U_original.csv', data_type, gs_file, gs_type))[:7] + ' |  ' + str(pearson(dataset_path + 'R_original.csv', data_type, gs_file, gs_type))[:7] + ' |  ' + str(pearson(dataset_path + 'B_original.csv', data_type, gs_file, gs_type))[:7] + '\n' \
            + 'Option 1 |  ' + str(pearson(dataset_path + 'U_option1.csv',  data_type, gs_file, gs_type))[:7] + ' |  ' + str(pearson(dataset_path + 'R_option1.csv',  data_type, gs_file, gs_type))[:7] + ' |  ' + str(pearson(dataset_path + 'B_option1.csv',  data_type, gs_file, gs_type))[:7] + '\n' \
            + 'Option 2 |  ' + str(pearson(dataset_path + 'U_option2.csv',  data_type, gs_file, gs_type))[:7] + ' |  ' + str(pearson(dataset_path + 'R_option2.csv',  data_type, gs_file, gs_type))[:7] + ' |  ' + str(pearson(dataset_path + 'B_option2.csv',  data_type, gs_file, gs_type))[:7] + '\n' \
            + 'Option 3 |  ' + str(pearson(dataset_path + 'U_option3.csv',  data_type, gs_file, gs_type))[:7] + ' |  ' + str(pearson(dataset_path + 'R_option3.csv',  data_type, gs_file, gs_type))[:7] + ' |  ' + str(pearson(dataset_path + 'B_option3.csv',  data_type, gs_file, gs_type))[:7] + '\n' \
            + 'Option 4 |  ' + str(pearson(dataset_path + 'U_option4.csv',  data_type, gs_file, gs_type))[:7] + ' |  ' + str(pearson(dataset_path + 'R_option4.csv',  data_type, gs_file, gs_type))[:7] + ' |  ' + str(pearson(dataset_path + 'B_option4.csv',  data_type, gs_file, gs_type))[:7] + '\n' \
            + 'Option 5 |  ' + str(pearson(dataset_path + 'U_option5.csv',  data_type, gs_file, gs_type))[:7] + ' |  ' + str(pearson(dataset_path + 'R_option5.csv',  data_type, gs_file, gs_type))[:7] + ' |  ' + str(pearson(dataset_path + 'B_option5.csv',  data_type, gs_file, gs_type))[:7] + '\n' \
            + 'Option 6 |  ' + str(pearson(dataset_path + 'U_option6.csv',  data_type, gs_file, gs_type))[:7] + ' |  ' + str(pearson(dataset_path + 'R_option6.csv',  data_type, gs_file, gs_type))[:7] + ' |  ' + str(pearson(dataset_path + 'B_option6.csv',  data_type, gs_file, gs_type))[:7] + '\n' \
            + 'Option 7 |  ' + str(pearson(dataset_path + 'U_option7.csv',  data_type, gs_file, gs_type))[:7] + ' |  ' + str(pearson(dataset_path + 'R_option7.csv',  data_type, gs_file, gs_type))[:7] + ' |  ' + str(pearson(dataset_path + 'B_option7.csv',  data_type, gs_file, gs_type))[:7] + '\n' 

def test(dataset_path, data_type, gs_file, gs_type):
    print '============================== ' + dataset_path.split(r'/')[-2] + ' =============================='
    print
    print evaluate(dataset_path + '/Aminul12/'  , data_type, gs_file, gs_type)
    print evaluate(dataset_path + '/Aminul08/'  , data_type, gs_file, gs_type)
    print evaluate(dataset_path + '/Mihalcea06/', data_type, gs_file, gs_type)

if __name__ == '__main__':
    # print pearson('pearson_sample.txt')
    # import glob
    # with open('../data/output/result.txt', 'w') as f:
    #     for folder_pathname in glob.glob("../data/output/*/"):
    #         print folder_pathname
    #         f.write(evaluate(folder_pathname))
    #         f.write('\n')
    # print pearson('../data/output/50-docs/original-GTM.csv')
    # print evaluate_old('../data/output/50-docs/')
    print 
    print '============================== Options =============================='
    print 
    print 'Original: rt'
    print 'Option 1: AVG(rt, imp1 + imp2)'
    print 'Option 2: AVG(rt, MAX(imp1, imp2))'
    print 'Option 3: rt * imp1 * imp2'
    print 'Option 4: AVG(rt, MIN(imp1, imp2))'
    print 'Option 5: AVG(rt, AVG(imp1, imp2))'
    print 'Option 6: rt * MAX(imp1, imp2)'
    print 'Option 7: rt * MIN(imp1, imp2)'
    print
    print
    test('../data/output/50-docs/'  , 'csv', '../resource/standard.csv'       , 'csv')
    test('../data/output/30-pairs/' , 'csv', '../data/gs/30-pairs-gt.txt'     , 'tsv')
    test('../data/output/131-pairs/', 'csv', '../data/gs/131-pairs-gt-rm2.txt', 'tsv')
    # STS 12'
    test('../data/SemEval2012/STS.output.MSRpar/'          , 'csv', '../data/SemEval2012/STS.gs.MSRpar.txt'          , 'tsv')
    test('../data/SemEval2012/STS.output.MSRvid/'          , 'csv', '../data/SemEval2012/STS.gs.MSRvid.txt'          , 'tsv')
    test('../data/SemEval2012/STS.output.SMTeuroparl/'     , 'csv', '../data/SemEval2012/STS.gs.SMTeuroparl.txt'     , 'tsv')
    test('../data/SemEval2012/STS.output.surprise.OnWN/'   , 'csv', '../data/SemEval2012/STS.gs.surprise.OnWN.txt'   , 'tsv')
    test('../data/SemEval2012/STS.output.surprise.SMTnews/', 'csv', '../data/SemEval2012/STS.gs.surprise.SMTnews.txt', 'tsv')
    # STS 13'
    test('../data/SemEval2013/STS.output.FNWN/'     , 'csv', '../data/SemEval2013/STS.gs.FNWN.txt'     , 'tsv')
    test('../data/SemEval2013/STS.output.headlines/', 'csv', '../data/SemEval2013/STS.gs.headlines.txt', 'tsv')
    test('../data/SemEval2013/STS.output.OnWN/'     , 'csv', '../data/SemEval2013/STS.gs.OnWN.txt'     , 'tsv')
    test('../data/SemEval2013/STS.output.SMT/'      , 'csv', '../data/SemEval2013/STS.gs.SMT.txt'      , 'tsv')
