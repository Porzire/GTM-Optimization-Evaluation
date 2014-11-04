import os

def cut_lines(in_file, out_dir):
    content = [line.strip() for line in open(in_file)]

    if not os.path.exists(out_dir):
        os.makedirs(out_dir)

    size = len(str(len(content)))

    for i in range(0, len(content)):
        index_size = len(str(i))
        zero_fill = size - index_size
        index_str = ('0' * zero_fill) + str(i)
        with open(out_dir + '/doc-' + index_str, 'w') as file:
            file.write(content[i])


if __name__ == '__main__':

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
        cut_lines(train_dir + input_file[:-4] + '.clean.txt', train_dir + input_file[:-4])

    # STS 13'
    train_dir = '../data/SemEval2013/'
    input_files = [
        'STS.input.FNWN.txt',
        'STS.input.headlines.txt',
        'STS.input.OnWN.txt',
        'STS.input.SMT.txt'
    ]
    for input_file in input_files:
        cut_lines(train_dir + input_file[:-4] + '.clean.txt', train_dir + input_file[:-4])
