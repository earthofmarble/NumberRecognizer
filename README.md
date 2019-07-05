# numberRecognizer
Распознает цифры.

Запускаем программу.
Начинается обучение сети. Придется подождать(минуту, иногда до пяти).
Сеть учится [на базе рукописных символов MNIST](http://yann.lecun.com/exdb/mnist/).
Файлы MNIST лежат в архиве MNIST.zip. 
Когда обучение заканчивается, начинается тест точности. Сеть проходит по тестам из той же базы символов. Выводит точность(в тестах должна быть 95-97%).
После можно вводить путь к ЛОКАЛЬНОМУ изображению !!! 28 на 28 пикселей !!!

Нарисованные мной цифры угадывала далеко не всегда. Можно поиграться, либо рисовать в пэйнте цифру, либо на бумаге. Если рисовать на бумаге - стоит нарисовать пожирнее, иначе при сжатии до 28 пикселей от нее ничего не останется. Не уверен почему она не всегда распознает нарисованное мной, но тесты проходит с вероятностью 96%. Программа, которая на питоне, показывает идентичные результаты, хотя там правильнее рандомились первоначальные веса.

Можно изменить количество эпох обучения с двух до пяти, точность немного(при прохождении тестов, а для собственных картинок может и много) возрастёт.

Не буду тут много расписывать, все задокументировано, если это можно так назвать, в коде.
В проекте лежит папка(TESTimages) с нарисованными мной картинками, можно посмотреть, если самому лень рисовать.

Сама нейронная сеть состоит из трех слоев (1 входной, 1 скрытый, 1 выходной). 784 нейрона на входном слое (по количеству пикселей в изображении 28*28), 200 скрытых нейронов (это оптимальное количество для данного случая), 10 выходных нейронов (так как у нас 10 возможных цифр).

![alt text](https://pp.userapi.com/c850220/v850220413/1761ff/TDLOKq2ozT8.jpg)

ОБНОВЛЕНИЕ.
Сделал обратный проход по сети. Когда сеть обучится и предложит указать путь к изображению, вместо пути, нужно ввести в консоль "backquery", указать цифру, в консоль будут выведены 784 значения через запятую. Копируем значения, вставялем в программу на Python(которая находится в папке Python, а папка в корневой директории). Можно было сделать вывод изображения чрерз джаву, но изображения нельзя выводить в консоль, а джаву фх и свинг я очень не люблю, /// можно было в браузер, кстати, надо попробовать /// , но пока так. В папке Python так же есть текстовый документ с готовыми значениями, уже полученными через джаву, осталось только вставить в питон. Что-то еще хотел написать, не помню. Результаты ниже:
![alt text](https://pp.userapi.com/c851236/v851236458/1563b5/00bjm6UOQrE.jpg)

ОБНОВЛЕНИЕ. 
Сделал вращение тренинговых картинок на +/-10 градусов. Это должно увеличить точность. В тесте (из файла с тестами mnist_test.csv) точность не изменилась, но, судя по тому, какие цифры рисует сеть, это повлияло в лучшую сторону. Для теста, скорее всего, выбраны определенные цифры, поэтому результат не заметен.
Я почитал, этот датасет не очень качественный. Там больше половины данных это трудноразличимые цифры, так что сеть учится понимать то, что трудно понять человеку, а то, что человеку легко - учит хуже. По крайней мере так пишут в интернете, сам я не проверял. А может быть, я неправильно рисовал свои картинки, на сайте [MNIST Database](http://yann.lecun.com/exdb/mnist/) написано:
>The original black and white (bilevel) images from NIST were size normalized to fit in a 20x20 pixel box while preserving their aspect ratio.
>The resulting images contain grey levels as a result of the anti-aliasing technique used by the normalization algorithm.
>The images were centered in a 28x28 image by computing the center of mass of the pixels, and translating the image so as to position this point at the center of the 28x28 field.

Тем не менее, оно работает. На этом этапе идеальная точность не является важнейшим приоритетом.

ОБНОВЛЕНИЕ.
Сегодня сделал совсем чуть-чуть - читал про другие виды сетей, их оказалось очень много, нужно будет разбираться.
- Сделал ресайз изображения, предоставляемого пользователем до 28*28, вне зависимости от исходного размера изображения.
- Проверил следующее: 
>The original black and white (bilevel) images from NIST were size normalized to fit in a 20x20 pixel box while preserving their aspect ratio

 Да, всё верно. Рисовать цифру нужно по центру, необходимо оставлять отступы по ~14% со всех сторон (для квадратного изображения, в случае с прямоугольным - отступы пропорционально увеличиваются, так как в конечном счете картинка станет квадратной).
 - Вроде всё. Говорю же, сегодня сделал мало.
 Завтра сделаю автоматическое определение положения цифры на холсте, чтобы можно было рисовать как и где угодно. Еще нужно сделать интерфейс, оооо и может даже если не выйдет сделать качетсвенный подгон положения цифры, можно сделать форму, на которой пользователь будет увеличивать/уменьшать картинку, хотя всё равно придется делать автоматическое, а почему - узнаете в следующих обновлениях.
 
ОБНОВЛЕНИЕ.
Это только кажется, что сегодня было сделано мало. На самом деле был проведен большой объем работы, интересно, почему говорят проведён, не важно. Так вот, ооой нет сил уже. Короче, резюмирую: сделал коррекцию размера и положения цифры на холсте. Теперь, где бы цифра не находилась, какого бы размера не была (ну, только тяжело ей определять цифры, TODO НУЖНО СНАЧАЛА НАХОДИТЬ ЦИФРУ, А ПОТОМ РЕСАЙЗИТЬ. ДОДЕЛАЮ. ей тяжело определять очень маленькие и написанные тонкой линией цифры, тут нужно войти в положение,они же всё-таки до 28*28 сжимаются, но я постараюсь это пофиксить) - сеть должна определить цифру. У нее какие-то проблемы с цифрой 6 ("шесть") <- это для нейросетей написал, мало ли. Ну эта проблема, скорее всего, связана с датасетом, я уже сделал... хотел написать ВСЁ, но напишу... МНОГОЕ возможное. Что еще написать, пока я не заснул. РАБОТАЕТ почти СУПЕР КЛАСС вот. Это я завтра перепишу, устал очень. А, еще она играется с контрастностью и яркостью картинки, чтобы максимально абстрагироваться от цвета бэкграунда, скорее это я игрался, подбирал более-менее дженериковые значения.

