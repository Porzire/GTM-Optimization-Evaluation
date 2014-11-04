def combine_line(in_files, out_file):
    """ Concatenate the same line in the different files.

    Args:
        in_files: An array of files to be concatenated.
        out_file: The pathname of the output file.
    """
    contents = []
    for in_file in in_files:
        contents.append([line.strip() for line in open(in_file)])
    with open(out_file, 'w') as file:
        for i in range(0, len(contents[0])):
            for content in contents:
                file.write(content[i])
            file.write('\n')


if __name__ == '__main__':
    import glob
    import os
    for folder_pathname in glob.glob("../data/output/*/"):
        combine_line([
            folder_pathname + 'original.txt',
            folder_pathname + 'option1.txt',
            folder_pathname + 'option2.txt',
            folder_pathname + 'option3.txt',
            folder_pathname + 'option4.txt'
        ], folder_pathname + 'result.csv')
        print folder_pathname + ' done'
